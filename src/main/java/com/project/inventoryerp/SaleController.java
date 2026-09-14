package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired private SaleRepository saleRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    @Transactional
    public Sale createSale(@Valid @RequestBody SaleRequest request) {
        Business business = currentUserService.getCurrentBusiness();

        Product product = productRepository.findById(request.getProductId())
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setQuantity(request.getQuantity());
        sale.setPrice(request.getPrice());
        sale.setBusiness(business);
        return saleRepository.save(sale);
    }

    @GetMapping
    public Page<Sale> getAllSales(Pageable pageable) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return saleRepository.findAllByBusinessId(businessId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return saleRepository.findById(id)
                .filter(s -> s.getBusiness() != null && s.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
