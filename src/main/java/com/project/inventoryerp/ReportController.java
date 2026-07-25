package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;
    @GetMapping("/total-sales")
    public BigDecimal getTotalSales() {
        List<Sale> sales = saleRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        for (Sale sale : sales) {
            BigDecimal saleTotal = sale.getPrice().multiply(BigDecimal.valueOf(sale.getQuantity()));
            total = total.add(saleTotal);
        }
        return total;
    }

    @GetMapping("/low-stock")
    public List<Inventory> getLowStock(@RequestParam(defaultValue = "10") Integer threshold) {
        List<Inventory> allInventory = inventoryRepository.findAll();
        return allInventory.stream()
                .filter(inv -> inv.getQuantity() < threshold)
                .collect(Collectors.toList());
    }

    @GetMapping("/product-count")
    public long getProductCount() {
        return productRepository.count();
    }
}