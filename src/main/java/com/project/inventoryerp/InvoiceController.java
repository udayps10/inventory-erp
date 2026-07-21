package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public Invoice createInvoice(@RequestBody InvoiceRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId()).get();
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).get();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setCustomer(customer);
        invoice.setPaymentMode(request.getPaymentMode());

        List<InvoiceItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId()).get();

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());
            items.add(item);

            BigDecimal itemCost = itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(itemCost);

            inventoryService.applyMovement(product, warehouse, -itemReq.getQuantity(), "SALE", invoice.getInvoiceNumber());
        }

        invoice.setItems(items);
        invoice.setTotalAmount(total);

        return invoiceRepository.save(invoice);
    }

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}