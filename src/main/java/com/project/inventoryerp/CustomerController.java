package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private CurrentUserService currentUserService;

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        customer.setBusiness(currentUserService.getCurrentBusiness());
        return customerRepository.save(customer);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return customerRepository.findAll().stream()
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return customerRepository.findById(id)
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        boolean exists = customerRepository.findById(id)
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .isPresent();
        if (!exists) return ResponseEntity.notFound().build();
        customerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}