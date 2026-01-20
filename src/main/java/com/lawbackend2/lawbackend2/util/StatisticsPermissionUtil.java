package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.exception.PermissionDeniedException;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatisticsPermissionUtil {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public StatisticsPermissionUtil(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new PermissionDeniedException("用户未登录");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        throw new PermissionDeniedException("无法获取当前用户ID");
    }

    public List<String> getCurrentUserRoles() {
        Long userId = getCurrentUserId();
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        return roleRepository.findAllById(roleIds).stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
    }

    public boolean hasRole(String roleCode) {
        List<String> userRoles = getCurrentUserRoles();
        return userRoles.contains(roleCode);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("SUPER_ADMIN");
    }

    public boolean isManager() {
        return hasRole("MANAGER") || isAdmin();
    }

    public boolean isStaff() {
        return hasRole("STAFF") || isManager();
    }

    public void checkStatisticsPermission() {
        if (!isStaff()) {
            throw new PermissionDeniedException("您没有权限查看统计数据");
        }
    }

    public void checkCaseAccessPermission(Long caseId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new PermissionDeniedException("用户不存在");
        }

        if (isManager()) {
            return;
        }

        throw new PermissionDeniedException("您没有权限访问该案件的统计数据");
    }
}
