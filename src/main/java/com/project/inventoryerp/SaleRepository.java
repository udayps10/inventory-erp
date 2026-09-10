package com.project.inventoryerp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.business.id = :businessId")
    Page<Sale> findAllByBusinessId(@Param("businessId") Long businessId, Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.business.id = :businessId")
    List<Sale> findAllByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(s.price * s.quantity), 0) FROM Sale s WHERE s.business.id = :businessId")
    BigDecimal sumTotalSales(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(s.price * s.quantity), 0) FROM Sale s WHERE s.business.id = :businessId AND s.saleDate BETWEEN :start AND :end")
    BigDecimal sumTotalSalesByDateRange(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.price * s.quantity), 0) FROM Sale s WHERE s.business.id = :businessId AND s.product.id = :productId")
    BigDecimal sumSalesByProduct(@Param("businessId") Long businessId, @Param("productId") Long productId);

    @Query("SELECT COALESCE(SUM(s.price * s.quantity), 0) FROM Sale s WHERE s.business.id = :businessId AND s.product.id = :productId AND s.saleDate BETWEEN :start AND :end")
    BigDecimal sumSalesByProductAndDateRange(@Param("businessId") Long businessId, @Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sale s WHERE s.business.id = :businessId AND s.product.id = :productId AND s.saleDate BETWEEN :start AND :end")
    Integer sumQuantityByProductAndDateRange(@Param("businessId") Long businessId, @Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Sale> findByBusinessIdAndSaleDateBetween(Long businessId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.product.id, COALESCE(SUM(s.price * s.quantity), 0) as revenue FROM Sale s WHERE s.business.id = :businessId AND s.saleDate BETWEEN :start AND :end GROUP BY s.product.id ORDER BY revenue DESC")
    List<Object[]> revenueByProductGrouped(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sale s WHERE s.business.id = :businessId")
    Integer sumTotalQuantitySold(@Param("businessId") Long businessId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Sale s WHERE s.business.id = :businessId AND s.saleDate BETWEEN :start AND :end")
    Integer sumTotalQuantitySoldByDateRange(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}