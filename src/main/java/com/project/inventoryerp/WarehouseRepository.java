package com.project.inventoryerp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    @Query("SELECT w FROM Warehouse w WHERE w.business.id = :businessId")
    Page<Warehouse> findAllByBusinessId(@Param("businessId") Long businessId, Pageable pageable);
}