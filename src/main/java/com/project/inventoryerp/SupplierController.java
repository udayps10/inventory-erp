package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired private SupplierRepository supplierRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        supplier.setBusiness(currentUserService.getCurrentBusiness());
        return supplierRepository.save(supplier);
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return supplierRepository.findAll().stream()
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return supplierRepository.findById(id)
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}