package com.project.inventoryerp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product;
    private Warehouse warehouse;
    private Business business;

    @BeforeEach
    void setUp() {
        business = new Business();
        business.setId(1L);
        business.setName("Test Business");

        product = new Product();
        product.setId(1L);
        product.setName("Widget");
        product.setSku("W001");
        product.setBusiness(business);

        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main Warehouse");
    }

    @Test
    void applyMovement_shouldCreateNewInventory_whenNotExist() {
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        StockMovement result = inventoryService.applyMovement(product, warehouse, 50, "PURCHASE", "PURCHASE-1");

        assertNotNull(result);
        assertEquals(50, result.getQuantityChange());
        assertEquals("PURCHASE", result.getReason());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void applyMovement_shouldIncreaseExistingInventory() {
        Inventory existing = new Inventory();
        existing.setId(1L);
        existing.setProduct(product);
        existing.setWarehouse(warehouse);
        existing.setQuantity(100);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(existing));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        inventoryService.applyMovement(product, warehouse, 25, "PURCHASE", "PURCHASE-2");

        assertEquals(125, existing.getQuantity());
    }

    @Test
    void applyMovement_shouldDecreaseInventoryOnSale() {
        Inventory existing = new Inventory();
        existing.setId(1L);
        existing.setProduct(product);
        existing.setWarehouse(warehouse);
        existing.setQuantity(100);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.of(existing));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        inventoryService.applyMovement(product, warehouse, -10, "SALE", "SALE-1");

        assertEquals(90, existing.getQuantity());
    }

    @Test
    void applyMovement_shouldSetBusinessOnNewStockMovement() {
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(inv -> inv.getArgument(0));

        StockMovement result = inventoryService.applyMovement(product, warehouse, 10, "PURCHASE", "PURCHASE-3");

        assertEquals(business, result.getBusiness());
    }
}
