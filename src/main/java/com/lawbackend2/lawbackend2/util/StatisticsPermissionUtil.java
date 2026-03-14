package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.exception.PermissionDeniedException;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
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
    private final WorkTeamMemberRepository workTeamMemberRepository;

    public StatisticsPermissionUtil(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, WorkTeamMemberRepository workTeamMemberRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
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

    public boolean isLawyer() {
        return hasRole("LAWYER") || isStaff();
    }

    public void checkStatisticsPermission() {
        if (!isLawyer()) {
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

        // 检查是否是工作团队成员
        List<WorkTeamMember> teamMembers = workTeamMemberRepository.findByCaseIdAndUserId(caseId, userId);
        if (!teamMembers.isEmpty()) {
            return;
        }

        throw new PermissionDeniedException("您没有权限访问该案件的统计数据");
    }

    public void checkCaseAccessPermission(Long caseId, Long caseCreateUserId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new PermissionDeniedException("用户不存在");
        }

        // 检查是否是案件创建者
        if (userId.equals(caseCreateUserId)) {
            return;
        }

        // 检查是否是工作团队成员
        List<WorkTeamMember> teamMembers = workTeamMemberRepository.findByCaseIdAndUserId(caseId, userId);
        if (!teamMembers.isEmpty()) {
            return;
        }

        throw new PermissionDeniedException("您没有权限访问该案件的统计数据");
    }

    public Long getCurrentUserCaseId(Long caseId) {
        if (isAdmin()) {
            return caseId;
        }

        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new PermissionDeniedException("用户不存在");
        }

        return caseId;
    }

    public List<Long> getAccessibleCaseIds() {
        if (isAdmin()) {
            return null;
        }

        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new PermissionDeniedException("用户不存在");
        }

        // 获取用户作为工作团队成员的案件ID列表
        List<Long> teamCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
        
        return teamCaseIds;
    }
}
