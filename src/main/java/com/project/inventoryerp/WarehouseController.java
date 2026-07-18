package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController

@RequestMapping("/api/warehouses")
public class WarehouseController {
	@Autowired
	private WarehouseRepository warehouseRepository;
	@PostMapping
	public Warehouse createWarehouse(@RequestBody Warehouse warehouse)
	{
		return warehouseRepository.save(warehouse);
		
	}

    @GetMapping
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
}
