package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private SaleRepository saleRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CurrentUserService currentUserService;

    @GetMapping("/total-sales")
    public BigDecimal getTotalSales() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        List<Sale> sales = saleRepository.findAll().stream()
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());

        BigDecimal total = BigDecimal.ZERO;
        for (Sale sale : sales) {
            total = total.add(sale.getPrice().multiply(BigDecimal.valueOf(sale.getQuantity())));
        }
        return total;
    }

    @GetMapping("/low-stock")
    public List<Inventory> getLowStock(@RequestParam(defaultValue = "10") Integer threshold) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return inventoryRepository.findAll().stream()
                .filter(inv -> inv.getProduct() != null && inv.getProduct().getBusiness() != null
                        && inv.getProduct().getBusiness().getId().equals(businessId))
                .filter(inv -> inv.getQuantity() < threshold)
                .collect(Collectors.toList());
    }

    @GetMapping("/product-count")
    public long getProductCount() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return productRepository.findAll().stream()
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(businessId))
                .count();
    }
}