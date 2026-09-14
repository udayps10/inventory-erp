package com.project.inventoryerp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String home() { return "redirect:/login"; }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/dashboard")
    public String dashboard() { return "dashboard"; }

    @GetMapping("/products")
    public String products() { return "products"; }

    @GetMapping("/warehouses")
    public String warehouses() { return "warehouses"; }

    @GetMapping("/stock")
    public String stock() { return "stock"; }

    @GetMapping("/sales")
    public String sales() { return "sales"; }

    @GetMapping("/reports")
    public String reports() { return "reports"; }
}
