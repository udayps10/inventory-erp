package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public Inventory createInventory(@RequestBody InventoryRequest request) {
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
        return inventoryRepository.save(inventory);
    }

    @GetMapping
    public List<Inventory> getAllInventory() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getProduct() != null && i.getProduct().getBusiness() != null
                        && i.getProduct().getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }
}