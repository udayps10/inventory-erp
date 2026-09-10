package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
    public Purchase createPurchase(@Valid @RequestBody PurchaseRequest request) {
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
    public Page<Purchase> getAllPurchases(Pageable pageable) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return purchaseRepository.findAllByBusinessId(businessId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return purchaseRepository.findById(id)
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
