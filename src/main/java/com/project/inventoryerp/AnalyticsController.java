package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired private SaleRepository saleRepository;
    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockMovementRepository stockMovementRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private CurrentUserService currentUserService;

    @GetMapping("/velocity")
    public List<Map<String, Object>> getStockVelocity(
            @RequestParam(defaultValue = "30") Integer days) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        List<Product> products = productRepository.findAllByBusinessId(businessId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Product product : products) {
            Integer totalSold = saleRepository.sumQuantityByProductAndDateRange(businessId, product.getId(), start, end);
            BigDecimal totalRevenue = saleRepository.sumSalesByProductAndDateRange(businessId, product.getId(), start, end);
            BigDecimal avgDailySales = totalDays > 0
                    ? BigDecimal.valueOf(totalSold).divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            int currentStock = inventoryRepository.findAllByBusinessId(businessId).stream()
                    .filter(inv -> inv.getProduct().getId().equals(product.getId()))
                    .mapToInt(Inventory::getQuantity)
                    .sum();

            String velocityRank;
            if (totalSold == 0) {
                velocityRank = "DEAD";
            } else if (avgDailySales.compareTo(new BigDecimal("10")) >= 0) {
                velocityRank = "FAST";
            } else if (avgDailySales.compareTo(new BigDecimal("2")) >= 0) {
                velocityRank = "MEDIUM";
            } else {
                velocityRank = "SLOW";
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("productId", product.getId());
            item.put("productName", product.getName());
            item.put("sku", product.getSku());
            item.put("currentStock", currentStock);
            item.put("totalSold", totalSold);
            item.put("totalRevenue", totalRevenue);
            item.put("avgDailySales", avgDailySales);
            item.put("velocityRank", velocityRank);
            item.put("daysOfStock", avgDailySales.compareTo(BigDecimal.ZERO) > 0
                    ? BigDecimal.valueOf(currentStock).divide(avgDailySales, 1, RoundingMode.HALF_UP)
                    : null);
            result.add(item);
        }

        result.sort((a, b) -> {
            BigDecimal revA = (BigDecimal) a.get("totalRevenue");
            BigDecimal revB = (BigDecimal) b.get("totalRevenue");
            return revB.compareTo(revA);
        });

        return result;
    }

    @GetMapping("/abc-analysis")
    public Map<String, List<Map<String, Object>>> getAbcAnalysis(
            @RequestParam(defaultValue = "90") Integer days) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> revenueByProduct = saleRepository.revenueByProductGrouped(businessId, start, end);
        Map<Long, BigDecimal> productRevenue = new LinkedHashMap<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Object[] row : revenueByProduct) {
            Long productId = (Long) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            productRevenue.put(productId, revenue);
            totalRevenue = totalRevenue.add(revenue);
        }

        List<Map<String, Object>> products = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : productRevenue.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product != null) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productId", product.getId());
                item.put("productName", product.getName());
                item.put("sku", product.getSku());
                item.put("revenue", entry.getValue());
                item.put("percentOfTotal", totalRevenue.compareTo(BigDecimal.ZERO) > 0
                        ? entry.getValue().multiply(new BigDecimal("100")).divide(totalRevenue, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);
                products.add(item);
            }
        }

        Map<String, List<Map<String, Object>>> abcClassification = new LinkedHashMap<>();
        List<Map<String, Object>> aItems = new ArrayList<>();
        List<Map<String, Object>> bItems = new ArrayList<>();
        List<Map<String, Object>> cItems = new ArrayList<>();

        BigDecimal cumulative = BigDecimal.ZERO;
        for (Map<String, Object> item : products) {
            BigDecimal percent = (BigDecimal) item.get("percentOfTotal");
            cumulative = cumulative.add(percent);

            if (cumulative.compareTo(new BigDecimal("80")) <= 0) {
                item.put("class", "A");
                aItems.add(item);
            } else if (cumulative.compareTo(new BigDecimal("95")) <= 0) {
                item.put("class", "B");
                bItems.add(item);
            } else {
                item.put("class", "C");
                cItems.add(item);
            }
        }

        abcClassification.put("A", aItems);
        abcClassification.put("B", bItems);
        abcClassification.put("C", cItems);
        abcClassification.put("summary", List.of(
                Map.of("class", "A", "description", "Top 80% revenue - High priority", "count", aItems.size()),
                Map.of("class", "B", "description", "Next 15% revenue - Medium priority", "count", bItems.size()),
                Map.of("class", "C", "description", "Last 5% revenue - Low priority", "count", cItems.size())
        ));

        return abcClassification;
    }

    @GetMapping("/dead-stock")
    public List<Map<String, Object>> getDeadStock(
            @RequestParam(defaultValue = "90") Integer days) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Product> products = productRepository.findAllByBusinessId(businessId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Product product : products) {
            Integer totalSold = saleRepository.sumQuantityByProductAndDateRange(businessId, product.getId(), start, end);
            if (totalSold == 0) {
                int currentStock = inventoryRepository.findAllByBusinessId(businessId).stream()
                        .filter(inv -> inv.getProduct().getId().equals(product.getId()))
                        .mapToInt(Inventory::getQuantity)
                        .sum();

                if (currentStock > 0) {
                    BigDecimal stockValue = product.getPrice().multiply(BigDecimal.valueOf(currentStock));
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("productId", product.getId());
                    item.put("productName", product.getName());
                    item.put("sku", product.getSku());
                    item.put("currentStock", currentStock);
                    item.put("unitPrice", product.getPrice());
                    item.put("stockValue", stockValue);
                    item.put("daysSinceLastSale", days);
                    result.add(item);
                }
            }
        }

        result.sort((a, b) -> ((BigDecimal) b.get("stockValue")).compareTo((BigDecimal) a.get("stockValue")));
        return result;
    }

    @GetMapping("/reorder-suggestions")
    public List<Map<String, Object>> getReorderSuggestions(
            @RequestParam(defaultValue = "30") Integer salesDays,
            @RequestParam(defaultValue = "7") Integer leadTimeDays) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(salesDays);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        List<Product> products = productRepository.findAllByBusinessId(businessId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Product product : products) {
            Integer totalSold = saleRepository.sumQuantityByProductAndDateRange(businessId, product.getId(), start, end);
            BigDecimal avgDailySales = totalDays > 0
                    ? BigDecimal.valueOf(totalSold).divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            int currentStock = inventoryRepository.findAllByBusinessId(businessId).stream()
                    .filter(inv -> inv.getProduct().getId().equals(product.getId()))
                    .mapToInt(Inventory::getQuantity)
                    .sum();

            int reorderPoint = avgDailySales.multiply(BigDecimal.valueOf(leadTimeDays))
                    .multiply(new BigDecimal("1.2")).intValue();

            if (currentStock <= reorderPoint && totalSold > 0) {
                int suggestedOrderQty = avgDailySales.multiply(BigDecimal.valueOf(salesDays)).intValue() - currentStock;
                if (suggestedOrderQty < 1) suggestedOrderQty = 1;

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("productId", product.getId());
                item.put("productName", product.getName());
                item.put("sku", product.getSku());
                item.put("currentStock", currentStock);
                item.put("avgDailySales", avgDailySales);
                item.put("reorderPoint", reorderPoint);
                item.put("suggestedOrderQty", suggestedOrderQty);
                item.put("estimatedCost", product.getPrice().multiply(BigDecimal.valueOf(suggestedOrderQty)));
                result.add(item);
            }
        }

        result.sort((a, b) -> {
            int stockA = (int) a.get("currentStock");
            int stockB = (int) b.get("currentStock");
            return Integer.compare(stockA, stockB);
        });

        return result;
    }

    @GetMapping("/sales-trend")
    public List<Map<String, Object>> getSalesTrend(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "30") Integer days) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Sale> sales = saleRepository.findByBusinessIdAndSaleDateBetween(businessId, start, end);

        Map<String, Map<String, Object>> trendMap = new LinkedHashMap<>();

        for (Sale sale : sales) {
            String key;
            if ("weekly".equals(period)) {
                key = sale.getSaleDate().toLocalDate().with(java.time.DayOfWeek.MONDAY).toString();
            } else if ("monthly".equals(period)) {
                key = sale.getSaleDate().getYear() + "-" + String.format("%02d", sale.getSaleDate().getMonthValue());
            } else {
                key = sale.getSaleDate().toLocalDate().toString();
            }

            trendMap.computeIfAbsent(key, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("date", k);
                entry.put("totalSales", BigDecimal.ZERO);
                entry.put("totalOrders", 0);
                entry.put("totalQuantity", 0);
                return entry;
            });

            Map<String, Object> entry = trendMap.get(key);
            BigDecimal saleAmount = sale.getPrice().multiply(BigDecimal.valueOf(sale.getQuantity()));
            entry.put("totalSales", ((BigDecimal) entry.get("totalSales")).add(saleAmount));
            entry.put("totalOrders", (int) entry.get("totalOrders") + 1);
            entry.put("totalQuantity", (int) entry.get("totalQuantity") + sale.getQuantity());
        }

        List<Map<String, Object>> result = new ArrayList<>(trendMap.values());
        result.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
        return result;
    }

    @GetMapping("/purchase-vs-sales")
    public List<Map<String, Object>> getPurchaseVsSales(
            @RequestParam(defaultValue = "30") Integer days) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Sale> sales = saleRepository.findByBusinessIdAndSaleDateBetween(businessId, start, end);
        List<Purchase> purchases = purchaseRepository.findByBusinessIdAndPurchaseDateBetween(businessId, start, end);

        Map<String, Map<String, Object>> trendMap = new LinkedHashMap<>();

        for (Sale sale : sales) {
            String key = sale.getSaleDate().toLocalDate().toString();
            trendMap.computeIfAbsent(key, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("date", k);
                entry.put("totalSales", BigDecimal.ZERO);
                entry.put("totalPurchases", BigDecimal.ZERO);
                return entry;
            });
            Map<String, Object> entry = trendMap.get(key);
            entry.put("totalSales", ((BigDecimal) entry.get("totalSales")).add(sale.getPrice().multiply(BigDecimal.valueOf(sale.getQuantity()))));
        }

        for (Purchase purchase : purchases) {
            String key = purchase.getPurchaseDate().toLocalDate().toString();
            trendMap.computeIfAbsent(key, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("date", k);
                entry.put("totalSales", BigDecimal.ZERO);
                entry.put("totalPurchases", BigDecimal.ZERO);
                return entry;
            });
            Map<String, Object> entry = trendMap.get(key);
            entry.put("totalPurchases", ((BigDecimal) entry.get("totalPurchases")).add(purchase.getPrice().multiply(BigDecimal.valueOf(purchase.getQuantity()))));
        }

        List<Map<String, Object>> result = new ArrayList<>(trendMap.values());
        result.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
        return result;
    }

    @GetMapping("/warehouse-stock")
    public List<Map<String, Object>> getWarehouseStock() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        List<Warehouse> warehouses = warehouseRepository.findAll().stream()
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(businessId))
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            List<Inventory> warehouseInventory = inventoryRepository.findAllByWarehouseId(warehouse.getId()).stream()
                    .filter(inv -> inv.getProduct().getBusiness().getId().equals(businessId))
                    .toList();

            int totalQuantity = warehouseInventory.stream().mapToInt(Inventory::getQuantity).sum();
            BigDecimal totalValue = warehouseInventory.stream()
                    .map(inv -> inv.getProduct().getPrice().multiply(BigDecimal.valueOf(inv.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long skuCount = warehouseInventory.stream()
                    .map(inv -> inv.getProduct().getId())
                    .distinct().count();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("warehouseId", warehouse.getId());
            item.put("warehouseName", warehouse.getName());
            item.put("location", warehouse.getLocation());
            item.put("totalSKU", skuCount);
            item.put("totalQuantity", totalQuantity);
            item.put("totalValue", totalValue);
            result.add(item);
        }

        return result;
    }
}
