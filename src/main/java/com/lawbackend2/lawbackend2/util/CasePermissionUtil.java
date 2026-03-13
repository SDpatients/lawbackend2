package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.exception.PermissionDeniedException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
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
public class CasePermissionUtil {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final BankruptCaseRepository bankrupCaseRepository;

    public CasePermissionUtil(UserRepository userRepository, 
                              UserRoleRepository userRoleRepository, 
                              RoleRepository roleRepository, 
                              WorkTeamMemberRepository workTeamMemberRepository,
                              BankruptCaseRepository bankrupCaseRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.bankrupCaseRepository = bankrupCaseRepository;
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

    public BankruptCase getCaseById(Long caseId) {
        return bankrupCaseRepository.findById(caseId)
                .orElseThrow(() -> new PermissionDeniedException(404, "案件不存在或已被删除"));
    }

    public void checkCaseAccessPermission(Long caseId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        BankruptCase caseInfo = getCaseById(caseId);

        if (userId.equals(caseInfo.getCreateUserId())) {
            return;
        }

        List<WorkTeamMember> teamMembers = workTeamMemberRepository.findByCaseIdAndUserId(caseId, userId);
        if (!teamMembers.isEmpty()) {
            return;
        }

        throw new PermissionDeniedException("您没有权限访问此案件");
    }

    public void checkCaseAccessPermission(BankruptCase caseInfo) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();

        if (userId.equals(caseInfo.getCreateUserId())) {
            return;
        }

        List<WorkTeamMember> teamMembers = workTeamMemberRepository.findByCaseIdAndUserId(caseInfo.getId(), userId);
        if (!teamMembers.isEmpty()) {
            return;
        }

        throw new PermissionDeniedException("您没有权限访问此案件");
    }

    public void checkCaseEditPermission(Long caseId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        BankruptCase caseInfo = getCaseById(caseId);

        if (userId.equals(caseInfo.getCreateUserId())) {
            return;
        }

        List<WorkTeamMember> teamMembers = workTeamMemberRepository.findByCaseIdAndUserId(caseId, userId);
        if (!teamMembers.isEmpty()) {
            return;
        }

        throw new PermissionDeniedException("您没有权限编辑此案件");
    }

    public void checkCaseDeletePermission(Long caseId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        BankruptCase caseInfo = getCaseById(caseId);

        if (userId.equals(caseInfo.getCreateUserId())) {
            return;
        }

        throw new PermissionDeniedException("您没有权限删除此案件");
    }

    public boolean hasCaseAccessPermission(Long caseId) {
        try {
            checkCaseAccessPermission(caseId);
            return true;
        } catch (PermissionDeniedException e) {
            return false;
        }
    }

    public boolean hasCaseEditPermission(Long caseId) {
        try {
            checkCaseEditPermission(caseId);
            return true;
        } catch (PermissionDeniedException e) {
            return false;
        }
    }
}
