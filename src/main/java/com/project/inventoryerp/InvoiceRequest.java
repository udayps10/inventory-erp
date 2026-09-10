package com.project.inventoryerp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class InvoiceRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotBlank(message = "Payment mode is required")
    private String paymentMode;

    @NotEmpty(message = "Invoice must have at least one item")
    @Valid
    private List<InvoiceItemRequest> items;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public List<InvoiceItemRequest> getItems() { return items; }
    public void setItems(List<InvoiceItemRequest> items) { this.items = items; }
}
