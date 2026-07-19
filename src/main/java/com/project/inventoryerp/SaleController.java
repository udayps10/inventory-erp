package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public Sale createSale(@RequestBody SaleRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setProduct(product);
        sale.setWarehouse(warehouse);
        sale.setQuantity(request.getQuantity());
        sale.setPrice(request.getPrice());
        Sale savedSale = saleRepository.save(sale);

        // Stock goes OUT — negative quantity change
        inventoryService.applyMovement(product, warehouse, -request.getQuantity(), "SALE", "SALE-" + savedSale.getId());

        return savedSale;
    }

    @GetMapping
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}