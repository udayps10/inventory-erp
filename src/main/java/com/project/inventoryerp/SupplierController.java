package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Page<Supplier> getAllSuppliers(Pageable pageable) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return supplierRepository.findAllByBusinessId(businessId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return supplierRepository.findById(id)
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier updated) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return supplierRepository.findById(id)
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .map(supplier -> {
                    supplier.setName(updated.getName());
                    supplier.setPhone(updated.getPhone());
                    supplier.setEmail(updated.getEmail());
                    supplier.setAddress(updated.getAddress());
                    return ResponseEntity.ok(supplierRepository.save(supplier));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        boolean exists = supplierRepository.findById(id)
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .isPresent();
        if (!exists) return ResponseEntity.notFound().build();
        supplierRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
