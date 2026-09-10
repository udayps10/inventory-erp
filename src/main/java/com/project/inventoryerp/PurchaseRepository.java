package com.project.inventoryerp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE p.business.id = :businessId")
    Page<Purchase> findAllByBusinessId(@Param("businessId") Long businessId, Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE p.business.id = :businessId")
    List<Purchase> findAllByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Purchase p WHERE p.business.id = :businessId")
    BigDecimal sumTotalPurchases(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Purchase p WHERE p.business.id = :businessId AND p.purchaseDate BETWEEN :start AND :end")
    BigDecimal sumTotalPurchasesByDateRange(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Purchase> findByBusinessIdAndPurchaseDateBetween(Long businessId, LocalDateTime start, LocalDateTime end);
}