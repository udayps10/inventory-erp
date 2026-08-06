package com.project.inventoryerp;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BusinessRepository extends JpaRepository<Business, Long> {
}