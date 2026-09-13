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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        AuthRequest registerReq = new AuthRequest();
        registerReq.setUsername("analyticsuser_" + System.nanoTime());
        registerReq.setPassword("password123");
        registerReq.setEmail("analytics@test.com");
        registerReq.setBusinessName("Analytics Test Business");

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
    void velocity_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/analytics/velocity")
                        .param("days", "30")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void abcAnalysis_shouldReturnResult() throws Exception {
        mockMvc.perform(get("/api/analytics/abc-analysis")
                        .param("days", "90")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.A").isArray())
                .andExpect(jsonPath("$.B").isArray())
                .andExpect(jsonPath("$.C").isArray());
    }

    @Test
    void deadStock_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/analytics/dead-stock")
                        .param("days", "90")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void reorderSuggestions_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/analytics/reorder-suggestions")
                        .param("salesDays", "30")
                        .param("leadTimeDays", "7")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void salesTrend_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/analytics/sales-trend")
                        .param("period", "daily")
                        .param("days", "30")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void purchaseVsSales_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/analytics/purchase-vs-sales")
                        .param("days", "30")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void warehouseStock_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/analytics/warehouse-stock")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
