package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired private SaleRepository saleRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private InventoryService inventoryService;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    @Transactional
    public Sale createSale(@RequestBody SaleRequest request) {
        Business business = currentUserService.getCurrentBusiness();

        Customer customer = customerRepository.findById(request.getCustomerId())
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Product product = productRepository.findById(request.getProductId())
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setProduct(product);
        sale.setWarehouse(warehouse);
        sale.setQuantity(request.getQuantity());
        sale.setPrice(request.getPrice());
        sale.setBusiness(business);
        Sale savedSale = saleRepository.save(sale);

        inventoryService.applyMovement(product, warehouse, -request.getQuantity(), "SALE", "SALE-" + savedSale.getId());
        return savedSale;
    }

    @GetMapping
    public List<Sale> getAllSales() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return saleRepository.findAll().stream()
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }
}