package com.project.inventoryerp;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/save-test-product")
    public Product saveTestProduct() {
        Product product = new Product();
        product.setName("Rice 1kg");
        product.setSku("RICE-1KG");
        product.setPrice(new BigDecimal("50.00"));
        return productRepository.save(product);
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam String name) {
        return "Hello " + name;
    }

    @GetMapping("/greeting-object")
    public GreetingResponse greetingObject() {
        return new GreetingResponse("Hello from ERP!", "2026-07-12");
    }

    @GetMapping("/status")
    public String status() {
        return "server is running";
    }
}