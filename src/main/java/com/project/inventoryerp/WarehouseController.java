package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public Warehouse createWarehouse(@RequestBody Warehouse warehouse) {
        warehouse.setBusiness(currentUserService.getCurrentBusiness());
        return warehouseRepository.save(warehouse);
    }

    @GetMapping
    public List<Warehouse> getAllWarehouses() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return warehouseRepository.findAll().stream()
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return warehouseRepository.findById(id)
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        boolean exists = warehouseRepository.findById(id)
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(businessId))
                .isPresent();
        if (!exists) return ResponseEntity.notFound().build();
        warehouseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}