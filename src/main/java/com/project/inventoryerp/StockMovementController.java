package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    @Autowired private InventoryService inventoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private StockMovementRepository stockMovementRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public StockMovement createMovement(@RequestBody MovementRequest request) {
        Business business = currentUserService.getCurrentBusiness();
        Product product = productRepository.findById(request.getProductId())
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        return inventoryService.applyMovement(product, warehouse, request.getQuantityChange(), request.getReason(), request.getReferenceId());
    }

    @GetMapping
    public List<StockMovement> getAllMovements() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return stockMovementRepository.findAll().stream()
                .filter(m -> m.getProduct() != null && m.getProduct().getBusiness() != null
                        && m.getProduct().getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }
}