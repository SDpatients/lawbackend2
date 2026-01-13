package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.exception.PermissionDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilTest {

    @Test
    void testGetCurrentUserId_WithAuthenticatedUser_ShouldReturnUserId() {
        Long expectedUserId = 123L;
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(expectedUserId, null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long actualUserId = SecurityUtil.getCurrentUserId();

        assertEquals(expectedUserId, actualUserId);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserId_WithUnauthenticatedUser_ShouldThrowException() {
        SecurityContextHolder.getContext().setAuthentication(null);

        PermissionDeniedException exception = assertThrows(
                PermissionDeniedException.class,
                SecurityUtil::getCurrentUserId
        );

        assertEquals("用户未登录", exception.getMessage());
        assertEquals(403, exception.getCode());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserId_WithNonLongPrincipal_ShouldThrowException() {
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken("not_a_long", null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        PermissionDeniedException exception = assertThrows(
                PermissionDeniedException.class,
                SecurityUtil::getCurrentUserId
        );

        assertEquals("无法获取当前用户ID", exception.getMessage());
        assertEquals(403, exception.getCode());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testIsAuthenticated_WithAuthenticatedUser_ShouldReturnTrue() {
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken("user", null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean result = SecurityUtil.isAuthenticated();

        assertTrue(result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testIsAuthenticated_WithNoAuthentication_ShouldReturnFalse() {
        SecurityContextHolder.getContext().setAuthentication(null);

        boolean result = SecurityUtil.isAuthenticated();

        assertFalse(result);

        SecurityContextHolder.clearContext();
    }
}
