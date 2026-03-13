package com.lawbackend2.lawbackend2.util;

import com.lawbackend2.lawbackend2.entity.Approval;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursement;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.exception.PermissionDeniedException;
import com.lawbackend2.lawbackend2.repository.ApprovalRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.ExpenseReimbursementRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionChecker {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final BankruptCaseRepository bankrupCaseRepository;
    private final ApprovalRepository approvalRepository;
    private final ExpenseReimbursementRepository expenseReimbursementRepository;

    public PermissionChecker(UserRoleRepository userRoleRepository, 
                             RoleRepository roleRepository, 
                             WorkTeamMemberRepository workTeamMemberRepository,
                             BankruptCaseRepository bankrupCaseRepository,
                             ApprovalRepository approvalRepository,
                             ExpenseReimbursementRepository expenseReimbursementRepository) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.bankrupCaseRepository = bankrupCaseRepository;
        this.approvalRepository = approvalRepository;
        this.expenseReimbursementRepository = expenseReimbursementRepository;
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

    public BankruptCase getCaseById(Long caseId) {
        return bankrupCaseRepository.findById(caseId)
                .orElseThrow(() -> new PermissionDeniedException(404, "案件不存在或已被删除"));
    }

    public Approval getApprovalById(Long approvalId) {
        return approvalRepository.findById(approvalId)
                .orElseThrow(() -> new PermissionDeniedException(404, "审批记录不存在或已被删除"));
    }

    public ExpenseReimbursement getReimbursementById(Long reimbursementId) {
        return expenseReimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new PermissionDeniedException(404, "报销单不存在或已被删除"));
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

    public void checkApprovalAccessPermission(Long approvalId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        Approval approval = getApprovalById(approvalId);

        if (userId.equals(approval.getCreateUserId())) {
            return;
        }

        if (userId.equals(approval.getApproverId())) {
            return;
        }

        if (approval.getCaseId() != null) {
            try {
                checkCaseAccessPermission(approval.getCaseId());
                return;
            } catch (PermissionDeniedException ignored) {
            }
        }

        throw new PermissionDeniedException("您没有权限查看此审批详情");
    }

    public void checkApprovalApprovePermission(Long approvalId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        Approval approval = getApprovalById(approvalId);

        if (userId.equals(approval.getApproverId())) {
            return;
        }

        throw new PermissionDeniedException("您没有权限审批此记录");
    }

    public void checkApprovalEditPermission(Long approvalId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        Approval approval = getApprovalById(approvalId);

        if (userId.equals(approval.getCreateUserId())) {
            return;
        }

        throw new PermissionDeniedException("您没有权限编辑此审批记录");
    }

    public void checkApprovalDeletePermission(Long approvalId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        Approval approval = getApprovalById(approvalId);

        if (userId.equals(approval.getCreateUserId())) {
            return;
        }

        throw new PermissionDeniedException("您没有权限删除此审批记录");
    }

    public void checkReimbursementAccessPermission(Long reimbursementId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        ExpenseReimbursement reimbursement = getReimbursementById(reimbursementId);

        if (userId.equals(reimbursement.getApplicantId())) {
            return;
        }

        throw new PermissionDeniedException("您没有权限查看此报销单");
    }

    public void checkReimbursementEditPermission(Long reimbursementId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        ExpenseReimbursement reimbursement = getReimbursementById(reimbursementId);

        if (userId.equals(reimbursement.getApplicantId())) {
            if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
                throw new PermissionDeniedException("报销单已提交审批，无法编辑");
            }
            return;
        }

        throw new PermissionDeniedException("您没有权限编辑此报销单");
    }

    public void checkReimbursementDeletePermission(Long reimbursementId) {
        if (isAdmin()) {
            return;
        }

        Long userId = getCurrentUserId();
        ExpenseReimbursement reimbursement = getReimbursementById(reimbursementId);

        if (userId.equals(reimbursement.getApplicantId())) {
            if (!"PENDING".equals(reimbursement.getApprovalStatus())) {
                throw new PermissionDeniedException("报销单已提交审批，无法删除");
            }
            return;
        }

        throw new PermissionDeniedException("您没有权限删除此报销单");
    }

    public void checkReimbursementApprovePermission(Long reimbursementId) {
        if (isAdmin()) {
            return;
        }

        throw new PermissionDeniedException("您没有权限审批此报销单");
    }

    public void checkArchiveAccessPermission(Long caseId) {
        checkCaseAccessPermission(caseId);
    }

    public void checkArchiveEditPermission(Long caseId) {
        checkCaseEditPermission(caseId);
    }

    public void checkArchiveDeletePermission(Long caseId) {
        checkCaseEditPermission(caseId);
    }
}
