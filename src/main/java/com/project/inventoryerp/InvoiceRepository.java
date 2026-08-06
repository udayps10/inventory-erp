package com.project.inventoryerp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    long countByBusiness_Id(Long businessId);
}