package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        category.setBusiness(currentUserService.getCurrentBusiness());
        return categoryRepository.save(category);
    }

    @GetMapping
    public List<Category> getAllCategories() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return categoryRepository.findAll().stream()
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return categoryRepository.findById(id)
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        boolean exists = categoryRepository.findById(id)
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .isPresent();
        if (!exists) return ResponseEntity.notFound().build();
        categoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}