package com.project.inventoryerp;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByBarcode(String barcode);

    Optional<Product> findByIdAndBusiness(Long id, Business business);

    @Query("SELECT p FROM Product p WHERE p.business.id = :businessId")
    List<Product> findAllByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT p FROM Product p WHERE p.business.id = :businessId")
    Page<Product> findAllByBusinessId(@Param("businessId") Long businessId, Pageable pageable);
}