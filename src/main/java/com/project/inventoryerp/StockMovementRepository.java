package com.project.inventoryerp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("SELECT sm FROM StockMovement sm WHERE sm.business.id = :businessId")
    Page<StockMovement> findAllByBusinessId(@Param("businessId") Long businessId, Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.business.id = :businessId")
    List<StockMovement> findAllByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.business.id = :businessId AND sm.createdAt BETWEEN :start AND :end")
    List<StockMovement> findAllByBusinessIdAndDateRange(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT sm.product.id, COALESCE(SUM(sm.quantityChange), 0) FROM StockMovement sm WHERE sm.business.id = :businessId AND sm.createdAt BETWEEN :start AND :end GROUP BY sm.product.id")
    List<Object[]> totalMovementByProductAndDateRange(@Param("businessId") Long businessId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}