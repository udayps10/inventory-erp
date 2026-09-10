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

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private SaleRepository saleRepository;
    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StockMovementRepository stockMovementRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private CurrentUserService currentUserService;

    @GetMapping("/total-sales")
    public Map<String, Object> getTotalSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        Map<String, Object> result = new LinkedHashMap<>();

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            BigDecimal total = saleRepository.sumTotalSalesByDateRange(businessId, start, end);
            Integer quantity = saleRepository.sumTotalQuantitySoldByDateRange(businessId, start, end);
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("totalSales", total);
            result.put("totalQuantitySold", quantity);
            result.put("periodDays", days);
            result.put("dailyAverage", days > 0 ? total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        } else {
            BigDecimal total = saleRepository.sumTotalSales(businessId);
            Integer quantity = saleRepository.sumTotalQuantitySold(businessId);
            result.put("totalSales", total);
            result.put("totalQuantitySold", quantity);
        }
        return result;
    }

    @GetMapping("/low-stock")
    public List<Map<String, Object>> getLowStock(
            @RequestParam(defaultValue = "10") Integer threshold,
            @RequestParam(required = false) Long warehouseId) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        List<Inventory> inventoryList;

        if (warehouseId != null) {
            inventoryList = inventoryRepository.findAllByWarehouseId(warehouseId).stream()
                    .filter(inv -> inv.getProduct().getBusiness().getId().equals(businessId))
                    .filter(inv -> inv.getQuantity() < threshold)
                    .toList();
        } else {
            inventoryList = inventoryRepository.findLowStockByBusinessId(businessId, threshold);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Inventory inv : inventoryList) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("inventoryId", inv.getId());
            item.put("productId", inv.getProduct().getId());
            item.put("productName", inv.getProduct().getName());
            item.put("sku", inv.getProduct().getSku());
            item.put("warehouseId", inv.getWarehouse().getId());
            item.put("warehouseName", inv.getWarehouse().getName());
            item.put("currentQuantity", inv.getQuantity());
            item.put("threshold", threshold);
            item.put("deficit", threshold - inv.getQuantity());
            result.add(item);
        }
        return result;
    }

    @GetMapping("/product-count")
    public Map<String, Object> getProductCount() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        long count = productRepository.findAllByBusinessId(businessId).size();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalProducts", count);
        return result;
    }

    @GetMapping("/total-purchases")
    public Map<String, Object> getTotalPurchases(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        Map<String, Object> result = new LinkedHashMap<>();

        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            BigDecimal total = purchaseRepository.sumTotalPurchasesByDateRange(businessId, start, end);
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("totalPurchases", total);
        } else {
            BigDecimal total = purchaseRepository.sumTotalPurchases(businessId);
            result.put("totalPurchases", total);
        }
        return result;
    }

    @GetMapping("/total-stock-value")
    public Map<String, Object> getTotalStockValue() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        List<Inventory> inventoryList = inventoryRepository.findAllByBusinessId(businessId);
        BigDecimal totalValue = BigDecimal.ZERO;
        int totalItems = 0;

        for (Inventory inv : inventoryList) {
            BigDecimal itemValue = inv.getProduct().getPrice().multiply(BigDecimal.valueOf(inv.getQuantity()));
            totalValue = totalValue.add(itemValue);
            totalItems += inv.getQuantity();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalStockValue", totalValue);
        result.put("totalStockItems", totalItems);
        result.put("totalSKUs", inventoryList.stream()
                .map(i -> i.getProduct().getId())
                .distinct().count());
        return result;
    }
}
