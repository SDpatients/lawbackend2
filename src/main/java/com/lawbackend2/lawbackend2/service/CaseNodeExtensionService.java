package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.CaseNodeExtension;

import java.util.List;

public interface CaseNodeExtensionService {

    CaseNodeExtension applyExtension(Long nodeInstanceId, Long caseId, Integer extensionDays, String applyReason, Long applyUserId, String applyUserName);

    CaseNodeExtension approveExtension(Long extensionId, Long approverId, String approverName, String approvalOpinion);

    CaseNodeExtension rejectExtension(Long extensionId, Long approverId, String approverName, String approvalOpinion);

    List<CaseNodeExtension> getExtensionsByNodeInstanceId(Long nodeInstanceId);

    List<CaseNodeExtension> getExtensionsByCaseId(Long caseId);

    List<CaseNodeExtension> getPendingApprovals();

    List<CaseNodeExtension> getPendingApprovalsByApproverId(Long approverId);

    CaseNodeExtension getExtensionById(Long id);
}
