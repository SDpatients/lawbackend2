package com.lawbackend2.lawbackend2.websocket;

import com.lawbackend2.lawbackend2.util.JwtTokenUtil;
import org.springframework.test.util.ReflectionTestUtils;

public class WebSocketStandaloneTest {
    
    public static void main(String[] args) {
        System.out.println("Starting WebSocket standalone test...\n");
        
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        Long testUserId = 1L;
        String testSecret = "lawbackend2-secret-key-for-jwt-token-generation-and-verification-must-be-at-least-256-bits";
        Long testExpiration = 180000000L;

        ReflectionTestUtils.setField(jwtTokenUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenExpiration", testExpiration);
        ReflectionTestUtils.setField(jwtTokenUtil, "refreshTokenExpiration", testExpiration);

        System.out.println("=== Test 1: Generate Token ===");
        testGenerateToken(jwtTokenUtil, testUserId);
        
        System.out.println("\n=== Test 2: Token Validation ===");
        testTokenValidation(jwtTokenUtil, testUserId);
        
        System.out.println("\n=== Test 3: Token Expiration Check ===");
        testTokenExpiration(jwtTokenUtil, testUserId);
        
        System.out.println("\n=== Test 4: Token Type Check ===");
        testTokenType(jwtTokenUtil, testUserId);
        
        System.out.println("\n=== Test 5: Get Token Claims ===");
        testGetClaims(jwtTokenUtil, testUserId);
        
        System.out.println("\nAll tests completed!");
    }
    
    private static void testGenerateToken(JwtTokenUtil jwtTokenUtil, Long userId) {
        try {
            String token = jwtTokenUtil.generateToken(userId, "testuser");
            System.out.println("SUCCESS: Token generated");
            System.out.println("  Token length: " + token.length());
            System.out.println("  Token prefix: " + token.substring(0, Math.min(50, token.length())) + "...");
        } catch (Exception e) {
            System.out.println("FAILED: Token generation failed - " + e.getMessage());
        }
    }
    
    private static void testTokenValidation(JwtTokenUtil jwtTokenUtil, Long userId) {
        try {
            String token = jwtTokenUtil.generateToken(userId, "testuser");
            boolean isValid = jwtTokenUtil.validateToken(token);
            System.out.println("SUCCESS: Token validation - " + (isValid ? "PASSED" : "FAILED"));
            
            boolean isValidWithUsername = jwtTokenUtil.validateToken(token, "testuser");
            System.out.println("SUCCESS: Token validation with username - " + (isValidWithUsername ? "PASSED" : "FAILED"));
        } catch (Exception e) {
            System.out.println("FAILED: Token validation error - " + e.getMessage());
        }
    }
    
    private static void testTokenExpiration(JwtTokenUtil jwtTokenUtil, Long userId) {
        try {
            String token = jwtTokenUtil.generateToken(userId, "testuser");
            boolean isExpired = jwtTokenUtil.isTokenExpired(token);
            System.out.println("SUCCESS: Token expiration check - " + (isExpired ? "EXPIRED" : "NOT EXPIRED"));
            
            String username = jwtTokenUtil.getUsernameFromToken(token);
            System.out.println("SUCCESS: Username - " + username);
            
            Long extractedUserId = jwtTokenUtil.getUserIdFromToken(token);
            System.out.println("SUCCESS: User ID - " + extractedUserId);
        } catch (Exception e) {
            System.out.println("FAILED: Token expiration check error - " + e.getMessage());
        }
    }
    
    private static void testTokenType(JwtTokenUtil jwtTokenUtil, Long userId) {
        try {
            String accessToken = jwtTokenUtil.generateAccessToken(userId, "testuser");
            String refreshToken = jwtTokenUtil.generateRefreshToken(userId, "testuser");
            
            boolean isAccessToken = jwtTokenUtil.isAccessToken(accessToken);
            boolean isRefreshToken = jwtTokenUtil.isRefreshToken(refreshToken);
            
            System.out.println("SUCCESS: AccessToken type check - " + (isAccessToken ? "CORRECT" : "INCORRECT"));
            System.out.println("SUCCESS: RefreshToken type check - " + (isRefreshToken ? "CORRECT" : "INCORRECT"));
        } catch (Exception e) {
            System.out.println("FAILED: Token type check error - " + e.getMessage());
        }
    }
    
    private static void testGetClaims(JwtTokenUtil jwtTokenUtil, Long userId) {
        try {
            String token = jwtTokenUtil.generateToken(userId, "testuser");
            var claims = jwtTokenUtil.getClaimsFromToken(token);
            
            System.out.println("SUCCESS: Claims retrieved");
            System.out.println("  Subject: " + claims.getSubject());
            System.out.println("  UserId: " + claims.get("userId"));
            System.out.println("  Username: " + claims.get("username"));
            System.out.println("  Type: " + claims.get("type"));
        } catch (Exception e) {
            System.out.println("FAILED: Claims retrieval error - " + e.getMessage());
        }
    }
}
