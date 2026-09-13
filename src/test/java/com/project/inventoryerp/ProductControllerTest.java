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
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        AuthRequest registerReq = new AuthRequest();
        registerReq.setUsername("productuser_" + System.nanoTime());
        registerReq.setPassword("password123");
        registerReq.setEmail("product@test.com");
        registerReq.setBusinessName("Product Test Business");

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
    void createProduct_shouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setName("Widget");
        product.setSku("W001");
        product.setPrice(new java.math.BigDecimal("99.99"));
        product.setBrand("Acme");
        product.setUnit("pcs");

        mockMvc.perform(post("/api/products")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Widget"))
                .andExpect(jsonPath("$.sku").value("W001"));
    }

    @Test
    void getAllProducts_shouldReturnPage() throws Exception {
        Product product = new Product();
        product.setName("Gadget");
        product.setSku("G001");
        product.setPrice(new java.math.BigDecimal("49.99"));

        mockMvc.perform(post("/api/products")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)));

        mockMvc.perform(get("/api/products")
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setName("Item");
        product.setSku("I001");
        product.setPrice(new java.math.BigDecimal("25.00"));

        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/products/" + id)
                        .header("Authorization", authHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        Product product = new Product();
        product.setName("Old Name");
        product.setSku("U001");
        product.setPrice(new java.math.BigDecimal("10.00"));

        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        Product updated = new Product();
        updated.setName("New Name");
        updated.setSku("U001");
        updated.setPrice(new java.math.BigDecimal("20.00"));

        mockMvc.perform(put("/api/products/" + id)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void deleteProduct_shouldReturn204() throws Exception {
        Product product = new Product();
        product.setName("ToDelete");
        product.setSku("D001");
        product.setPrice(new java.math.BigDecimal("5.00"));

        MvcResult result = mockMvc.perform(post("/api/products")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/products/" + id)
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProductById_withNonExistentId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/products/99999")
                        .header("Authorization", authHeader()))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_withoutAuth_shouldReturn403() throws Exception {
        Product product = new Product();
        product.setName("NoAuth");
        product.setSku("NA001");
        product.setPrice(new java.math.BigDecimal("10.00"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isForbidden());
    }
}
