package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Page<Customer> getAllCustomers(Pageable pageable) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return customerRepository.findAllByBusinessId(businessId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return customerRepository.findById(id)
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updated) {
        Long businessId = currentUserService.getCurrentBusiness().getId();
        return customerRepository.findById(id)
                .filter(c -> c.getBusiness() != null && c.getBusiness().getId().equals(businessId))
                .map(customer -> {
                    customer.setName(updated.getName());
                    customer.setPhone(updated.getPhone());
                    customer.setEmail(updated.getEmail());
                    customer.setGstin(updated.getGstin());
                    customer.setState(updated.getState());
                    return ResponseEntity.ok(customerRepository.save(customer));
                })
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
