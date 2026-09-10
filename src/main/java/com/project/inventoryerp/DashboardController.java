package com.project.inventoryerp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/stock")
    public String stockLevels() {
        return "stock";
    }

    @GetMapping("/analytics/velocity")
    public String velocity() {
        return "velocity";
    }

    @GetMapping("/analytics/abc")
    public String abcAnalysis() {
        return "abc";
    }

    @GetMapping("/analytics/dead-stock")
    public String deadStock() {
        return "dead-stock";
    }

    @GetMapping("/analytics/reorder")
    public String reorder() {
        return "reorder";
    }

    @GetMapping("/analytics/sales-trend")
    public String salesTrend() {
        return "sales-trend";
    }

    @GetMapping("/analytics/purchase-vs-sales")
    public String purchaseVsSales() {
        return "purchase-vs-sales";
    }

    @GetMapping("/analytics/warehouse-stock")
    public String warehouseStock() {
        return "warehouse-stock";
    }
}
