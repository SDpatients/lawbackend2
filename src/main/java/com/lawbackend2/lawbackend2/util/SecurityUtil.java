package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.exception.PermissionDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new PermissionDeniedException("用户未登录");
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof Long) {
                return (Long) principal;
            }
            throw new PermissionDeniedException("无法获取当前用户ID");
        } catch (Exception e) {
            throw new PermissionDeniedException("获取用户信息失败: " + e.getMessage());
        }
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
    }
}