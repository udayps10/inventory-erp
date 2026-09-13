package com.project.inventoryerp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        try {
            var field = JwtUtil.class.getDeclaredField("secret");
            field.setAccessible(true);
            field.set(jwtUtil, "testSecretKeyForTestingOnly1234567890");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        jwtUtil.init();
    }

    @Test
    void generateToken_shouldReturnNonEmptyString() {
        String token = jwtUtil.generateToken("testuser");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("john");
        String username = jwtUtil.extractUsername(token);
        assertEquals("john", username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("testuser");
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    @Test
    void extractUsername_shouldThrowForInvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.extractUsername("garbage"));
    }

    @Test
    void generateToken_differentUsers_shouldProduceDifferentTokens() {
        String token1 = jwtUtil.generateToken("alice");
        String token2 = jwtUtil.generateToken("bob");
        assertNotEquals(token1, token2);
    }

    @Test
    void generateToken_sameUser_shouldProduceDifferentTokens() throws Exception {
        String token1 = jwtUtil.generateToken("alice");
        Thread.sleep(1100);
        String token2 = jwtUtil.generateToken("alice");
        assertNotEquals(token1, token2);
    }
}
