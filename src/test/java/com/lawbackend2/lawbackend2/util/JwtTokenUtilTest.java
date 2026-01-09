package com.lawbackend2.lawbackend2.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    private static final String TEST_USERNAME = "testuser";
    private static final Long TEST_USER_ID = 12345L;
    private static final String TEST_SECRET = "test-secret-key-for-jwt-token-generation-and-verification-must-be-at-least-256-bits-long-enough-for-hs512-algorithm";
    private static final Long TEST_EXPIRATION = 604800000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", TEST_EXPIRATION);
    }

    @Test
    void testGenerateToken() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        String username = jwtTokenUtil.getUsernameFromToken(token);

        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void testGetUserIdFromToken() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);

        assertEquals(TEST_USER_ID, userId);
    }

    @Test
    void testGetExpirationDateFromToken() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);

        assertNotNull(expirationDate);
        Date now = new Date();
        assertTrue(expirationDate.after(now));
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        boolean isValid = jwtTokenUtil.validateToken(token, TEST_USERNAME);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        boolean isValid = jwtTokenUtil.validateToken(token, "wronguser");

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", -1000L);
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", TEST_EXPIRATION);

        boolean isValid = jwtTokenUtil.validateToken(token, TEST_USERNAME);

        assertFalse(isValid);
    }

    @Test
    void testIsTokenExpired_NotExpired() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        boolean isExpired = jwtTokenUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void testGetClaimsFromToken() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        Claims claims = jwtTokenUtil.getClaimsFromToken(token);

        assertNotNull(claims);
        assertEquals(TEST_USERNAME, claims.getSubject());
        assertEquals(TEST_USER_ID, claims.get("userId", Long.class));
    }

    @Test
    void testGetExpiration() {
        Long expiration = jwtTokenUtil.getAccessTokenExpiration();

        assertEquals(TEST_EXPIRATION, expiration);
    }

    @Test
    void testGenerateTokenWithCustomClaims() {
        java.util.Map<String, Object> customClaims = new java.util.HashMap<>();
        customClaims.put("customKey", "customValue");

        String token = jwtTokenUtil.generateToken(customClaims, TEST_USERNAME, TEST_EXPIRATION);

        assertNotNull(token);
        Claims claims = jwtTokenUtil.getClaimsFromToken(token);
        assertEquals("customValue", claims.get("customKey"));
    }
}
