package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    @Autowired private InventoryService inventoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private StockMovementRepository stockMovementRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public StockMovement createMovement(@Valid @RequestBody MovementRequest request) {
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
    public Page<StockMovement> getAllMovements(Pageable pageable) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return stockMovementRepository.findAllByBusinessId(businessId, pageable);
    }
}
