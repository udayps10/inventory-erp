package com.project.inventoryerp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void register_shouldReturnToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser1");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setBusinessName("Test Business");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void register_withDuplicateUsername_shouldFail() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("duplicateuser");
        request.setPassword("password123");
        request.setEmail("dup@example.com");
        request.setBusinessName("Business 1");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        AuthRequest request2 = new AuthRequest();
        request2.setUsername("duplicateuser");
        request2.setPassword("password456");
        request2.setEmail("dup2@example.com");
        request2.setBusinessName("Business 2");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withBlankUsername_shouldFail() throws Exception {
        Map<String, String> request = Map.of(
                "username", "",
                "password", "password123",
                "businessName", "Biz"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withShortPassword_shouldFail() throws Exception {
        Map<String, String> request = Map.of(
                "username", "shortpwduser",
                "password", "123",
                "businessName", "Biz"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        AuthRequest registerReq = new AuthRequest();
        registerReq.setUsername("logintest");
        registerReq.setPassword("password123");
        registerReq.setEmail("login@example.com");
        registerReq.setBusinessName("Login Business");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)));

        AuthRequest loginReq = new AuthRequest();
        loginReq.setUsername("logintest");
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withWrongPassword_shouldFail() throws Exception {
        AuthRequest registerReq = new AuthRequest();
        registerReq.setUsername("wrongpwduser");
        registerReq.setPassword("password123");
        registerReq.setEmail("wrong@example.com");
        registerReq.setBusinessName("Wrong Pwd Business");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)));

        AuthRequest loginReq = new AuthRequest();
        loginReq.setUsername("wrongpwduser");
        loginReq.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withNonExistentUser_shouldFail() throws Exception {
        AuthRequest loginReq = new AuthRequest();
        loginReq.setUsername("nonexistent");
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isBadRequest());
    }
}
