package com.project.inventoryerp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    @Query("SELECT i FROM Inventory i WHERE i.product.business.id = :businessId")
    List<Inventory> findAllByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT i FROM Inventory i WHERE i.product.business.id = :businessId")
    Page<Inventory> findAllByBusinessId(@Param("businessId") Long businessId, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE i.product.business.id = :businessId AND i.quantity < :threshold")
    List<Inventory> findLowStockByBusinessId(@Param("businessId") Long businessId, @Param("threshold") Integer threshold);

    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId")
    List<Inventory> findAllByWarehouseId(@Param("warehouseId") Long warehouseId);
}