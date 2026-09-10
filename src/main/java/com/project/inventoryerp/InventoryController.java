package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public Inventory createInventory(@Valid @RequestBody InventoryRequest request) {
        Business business = currentUserService.getCurrentBusiness();
        Product product = productRepository.findById(request.getProductId())
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(request.getQuantity());
        inventory.setBusiness(business);
        return inventoryRepository.save(inventory);
    }

    @GetMapping
    public Page<Inventory> getAllInventory(Pageable pageable) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return inventoryRepository.findAllByBusinessId(businessId, pageable);
    }
}
