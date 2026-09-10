package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private StockMovementRepository stockMovementRepository;

    @Transactional
    public StockMovement applyMovement(Product product, Warehouse warehouse, Integer quantityChange, String reason, String referenceId) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElse(null);

        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setWarehouse(warehouse);
            inventory.setQuantity(0);
            inventory.setBusiness(product.getBusiness());
        }

        inventory.setQuantity(inventory.getQuantity() + quantityChange);
        inventoryRepository.save(inventory);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setBusiness(product.getBusiness());
        movement.setQuantityChange(quantityChange);
        movement.setReason(reason);
        movement.setReferenceId(referenceId);

        return stockMovementRepository.save(movement);
    }
}
