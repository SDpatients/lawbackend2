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
        ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenExpiration", TEST_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenUtil, "refreshTokenExpiration", TEST_EXPIRATION * 2);
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
        ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenExpiration", -1000L);
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenExpiration", TEST_EXPIRATION);

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

    @Test
    void testGenerateAccessToken() {
        String token = jwtTokenUtil.generateAccessToken(TEST_USER_ID, TEST_USERNAME);

        assertNotNull(token);
        assertTrue(jwtTokenUtil.isAccessToken(token));
        assertFalse(jwtTokenUtil.isRefreshToken(token));
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtTokenUtil.generateRefreshToken(TEST_USER_ID, TEST_USERNAME);

        assertNotNull(token);
        assertTrue(jwtTokenUtil.isRefreshToken(token));
        assertFalse(jwtTokenUtil.isAccessToken(token));
    }

    @Test
    void testValidateTokenWithoutUsername() {
        String token = jwtTokenUtil.generateToken(TEST_USER_ID, TEST_USERNAME);
        boolean isValid = jwtTokenUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        boolean isValid = jwtTokenUtil.validateToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    void testGetTokenType() {
        String accessToken = jwtTokenUtil.generateAccessToken(TEST_USER_ID, TEST_USERNAME);
        String refreshToken = jwtTokenUtil.generateRefreshToken(TEST_USER_ID, TEST_USERNAME);

        assertEquals("ACCESS", jwtTokenUtil.getTokenType(accessToken));
        assertEquals("REFRESH", jwtTokenUtil.getTokenType(refreshToken));
    }

    @Test
    void testGetRefreshTokenExpiration() {
        Long expiration = jwtTokenUtil.getRefreshTokenExpiration();

        assertEquals(TEST_EXPIRATION * 2, expiration);
    }
}
