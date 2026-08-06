package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private InventoryService inventoryService;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    @Transactional
    public Purchase createPurchase(@RequestBody PurchaseRequest request) {
        Business business = currentUserService.getCurrentBusiness();

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        Product product = productRepository.findById(request.getProductId())
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setProduct(product);
        purchase.setWarehouse(warehouse);
        purchase.setQuantity(request.getQuantity());
        purchase.setPrice(request.getPrice());
        purchase.setBusiness(business);
        Purchase savedPurchase = purchaseRepository.save(purchase);

        inventoryService.applyMovement(product, warehouse, request.getQuantity(), "PURCHASE", "PURCHASE-" + savedPurchase.getId());
        return savedPurchase;
    }

    @GetMapping
    public List<Purchase> getAllPurchases() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return purchaseRepository.findAll().stream()
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }
}