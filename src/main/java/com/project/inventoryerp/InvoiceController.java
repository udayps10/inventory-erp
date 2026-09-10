package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private WarehouseRepository warehouseRepository;
    @Autowired private InventoryService inventoryService;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    @Transactional
    public Invoice createInvoice(@Valid @RequestBody InvoiceRequest request) {
        Business business = currentUserService.getCurrentBusiness();

        Customer customer = customerRepository.findById(request.getCustomerId())
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(w -> w.getBusiness() != null && w.getBusiness().getId().equals(business.getId()))
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Invoice invoice = new Invoice();
        invoice.setBusiness(business);

        long invoiceCount = invoiceRepository.countByBusiness_Id(business.getId());
        invoice.setInvoiceNumber(String.format("INV-%04d", invoiceCount + 1));
        invoice.setCustomer(customer);
        invoice.setPaymentMode(request.getPaymentMode());

        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (InvoiceItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .filter(p -> p.getBusiness() != null && p.getBusiness().getId().equals(business.getId()))
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());

            BigDecimal taxableAmount = itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            item.setTaxableAmount(taxableAmount);

            BigDecimal gstPercent = product.getGstPercent() != null ? product.getGstPercent() : BigDecimal.ZERO;
            item.setGstPercent(gstPercent);

            BigDecimal halfGst = gstPercent.divide(BigDecimal.valueOf(2));
            BigDecimal cgstAmount = taxableAmount.multiply(halfGst).divide(BigDecimal.valueOf(100));
            BigDecimal sgstAmount = cgstAmount;

            item.setCgstAmount(cgstAmount);
            item.setSgstAmount(sgstAmount);

            BigDecimal lineTotal = taxableAmount.add(cgstAmount).add(sgstAmount);
            item.setTotalAmount(lineTotal);

            items.add(item);
            grandTotal = grandTotal.add(lineTotal);

            inventoryService.applyMovement(product, warehouse, -itemReq.getQuantity(), "SALE", invoice.getInvoiceNumber());
        }

        invoice.setItems(items);
        invoice.setTotalAmount(grandTotal);
        return invoiceRepository.save(invoice);
    }

    @GetMapping
    public Page<Invoice> getAllInvoices(Pageable pageable) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return invoiceRepository.findAllByBusinessId(businessId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return invoiceRepository.findById(id)
                .filter(i -> i.getBusiness() != null && i.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
