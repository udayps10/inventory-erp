package com.project.inventoryerp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        AuthRequest registerReq = new AuthRequest();
        registerReq.setUsername("reportuser_" + System.nanoTime());
        registerReq.setPassword("password123");
        registerReq.setEmail("report@test.com");
        registerReq.setBusinessName("Report Test Business");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseBody).get("token").asText();
    }

    private String authHeader() {
        return "Bearer " + authToken;
    }

    @Test
    void getTotalSales_shouldReturnZero() throws Exception {
        mockMvc.perform(get("/api/reports/total-sales")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(0));
    }

    @Test
    void getTotalSales_withDateRange_shouldReturnResult() throws Exception {
        mockMvc.perform(get("/api/reports/total-sales")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-12-31")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").exists());
    }

    @Test
    void getTotalPurchases_shouldReturnZero() throws Exception {
        mockMvc.perform(get("/api/reports/total-purchases")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPurchases").value(0));
    }

    @Test
    void getLowStock_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/reports/low-stock")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getProductCount_shouldReturnZero() throws Exception {
        mockMvc.perform(get("/api/reports/product-count")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(0));
    }

    @Test
    void getTotalStockValue_shouldReturnZero() throws Exception {
        mockMvc.perform(get("/api/reports/total-stock-value")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStockValue").value(0));
    }
}
