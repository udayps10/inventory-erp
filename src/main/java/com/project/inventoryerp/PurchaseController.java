package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public Purchase createPurchase(@RequestBody PurchaseRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        // 1. Save the purchase record
        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setProduct(product);
        purchase.setWarehouse(warehouse);
        purchase.setQuantity(request.getQuantity());
        purchase.setPrice(request.getPrice());
        Purchase savedPurchase = purchaseRepository.save(purchase);

        // 2. Use InventoryService to update stock + log movement — SAME method from last lesson
        inventoryService.applyMovement(product, warehouse, request.getQuantity(), "PURCHASE", "PURCHASE-" + savedPurchase.getId());

        return savedPurchase;
    }

    @GetMapping
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }
}