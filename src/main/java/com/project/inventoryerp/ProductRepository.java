package com.project.inventoryerp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>{
    Optional<Product> findByBarcode(String barcode);

    Optional<Product> findByIdAndBusiness(Long id, Business business);
}