package com.lawbackend2.lawbackend2.websocket;

import com.lawbackend2.lawbackend2.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class WebSocketIntegrationTest {

    private JwtTokenUtil jwtTokenUtil;
    private String token;
    private Long testUserId;

    @BeforeEach
    public void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        testUserId = 1L;
        String testSecret = "lawbackend2-secret-key-for-jwt-token-generation-and-verification-must-be-at-least-256-bits";
        Long testExpiration = 180000000L;

        ReflectionTestUtils.setField(jwtTokenUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenExpiration", testExpiration);
        ReflectionTestUtils.setField(jwtTokenUtil, "refreshTokenExpiration", testExpiration);

        String generatedToken = jwtTokenUtil.generateToken(testUserId, "testuser");
        token = "Bearer " + generatedToken;
    }

    @Test
    public void testGenerateToken() {
        String generatedToken = jwtTokenUtil.generateToken(testUserId, "testuser");
        
        assertNotNull(generatedToken);
        assertFalse(generatedToken.isEmpty());
        
        String username = jwtTokenUtil.getUsernameFromToken(generatedToken);
        assertEquals("testuser", username);
        
        Long userId = jwtTokenUtil.getUserIdFromToken(generatedToken);
        assertEquals(testUserId, userId);
    }

    @Test
    public void testTokenValidation() {
        String generatedToken = jwtTokenUtil.generateToken(testUserId, "testuser");
        
        boolean isValid = jwtTokenUtil.validateToken(generatedToken);
        assertTrue(isValid);
        
        boolean isValidWithUsername = jwtTokenUtil.validateToken(generatedToken, "testuser");
        assertTrue(isValidWithUsername);
    }

    @Test
    public void testTokenWithBearerPrefix() {
        String generatedToken = jwtTokenUtil.generateToken(testUserId, "testuser");
        String tokenWithBearer = "Bearer " + generatedToken;
        
        assertNotNull(tokenWithBearer);
        assertTrue(tokenWithBearer.startsWith("Bearer "));
    }

    @Test
    public void testTokenExpiration() {
        String generatedToken = jwtTokenUtil.generateToken(testUserId, "testuser");
        
        boolean isExpired = jwtTokenUtil.isTokenExpired(generatedToken);
        assertFalse(isExpired, "Token should not be expired");
    }

    @Test
    public void testGetClaimsFromToken() {
        String generatedToken = jwtTokenUtil.generateToken(testUserId, "testuser");
        
        var claims = jwtTokenUtil.getClaimsFromToken(generatedToken);
        
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals(testUserId, claims.get("userId", Long.class));
    }

    @Test
    public void testGenerateAccessToken() {
        String accessToken = jwtTokenUtil.generateAccessToken(testUserId, "testuser");
        
        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());
        
        String username = jwtTokenUtil.getUsernameFromToken(accessToken);
        assertEquals("testuser", username);
    }

    @Test
    public void testGenerateRefreshToken() {
        String refreshToken = jwtTokenUtil.generateRefreshToken(testUserId, "testuser");
        
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        assertEquals("testuser", username);
    }

    @Test
    public void testTokenType() {
        String accessToken = jwtTokenUtil.generateAccessToken(testUserId, "testuser");
        String refreshToken = jwtTokenUtil.generateRefreshToken(testUserId, "testuser");
        
        assertTrue(jwtTokenUtil.isAccessToken(accessToken));
        assertTrue(jwtTokenUtil.isRefreshToken(refreshToken));
        assertFalse(jwtTokenUtil.isAccessToken(refreshToken));
        assertFalse(jwtTokenUtil.isRefreshToken(accessToken));
    }
}
