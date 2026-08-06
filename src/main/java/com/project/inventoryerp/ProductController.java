package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        product.setBusiness(currentUserService.getCurrentBusiness());
        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        Business business = currentUserService.getCurrentBusiness();
        return productRepository.findAll().stream()
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Business business = currentUserService.getCurrentBusiness();
        return productRepository.findById(id)
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Business business = currentUserService.getCurrentBusiness();
        return productRepository.findById(id)
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setSku(updatedProduct.getSku());
                    product.setPrice(updatedProduct.getPrice());
                    product.setBrand(updatedProduct.getBrand());
                    product.setUnit(updatedProduct.getUnit());
                    product.setBarcode(updatedProduct.getBarcode());
                    product.setGstPercent(updatedProduct.getGstPercent());
                    product.setHsnCode(updatedProduct.getHsnCode());
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Business business = currentUserService.getCurrentBusiness();
        Product product = productRepository.findById(id)
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/barcode/{code}")
    public ResponseEntity<Product> getProductByBarcode(@PathVariable String code) {
        Business business = currentUserService.getCurrentBusiness();
        return productRepository.findByBarcode(code)
                .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}