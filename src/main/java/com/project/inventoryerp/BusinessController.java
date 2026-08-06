package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    @Autowired
    private BusinessRepository businessRepository;

    @PostMapping
    public Business createBusiness(@RequestBody Business business) {
        return businessRepository.save(business);
    }

    @GetMapping
    public List<Business> getAllBusiness() {
        return businessRepository.findAll();
    }
}