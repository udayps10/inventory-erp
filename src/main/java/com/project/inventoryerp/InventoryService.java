package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    public StockMovement applyMovement(Product product, Warehouse warehouse, Integer quantityChange, String reason, String referenceId) {

        // 1. Try to find an existing inventory row for this product+warehouse
        Inventory inventory = findInventory(product, warehouse);

        // 2. If none exists, create a new one starting at 0
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setWarehouse(warehouse);
            inventory.setQuantity(0);
        }

        // 3. Update the quantity
        inventory.setQuantity(inventory.getQuantity() + quantityChange);
        inventoryRepository.save(inventory);

        // 4. Record what happened (history)
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setQuantityChange(quantityChange);
        movement.setReason(reason);
        movement.setReferenceId(referenceId);

        return stockMovementRepository.save(movement);
    }

    private Inventory findInventory(Product product, Warehouse warehouse) {
        List<Inventory> allInventory = inventoryRepository.findAll();
        for (Inventory inv : allInventory) {
            if (inv.getProduct().getId().equals(product.getId())
                    && inv.getWarehouse().getId().equals(warehouse.getId())) {
                return inv;
            }
        }
        return null;
    }
}