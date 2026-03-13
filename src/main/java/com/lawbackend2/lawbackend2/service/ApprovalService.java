package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.ApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ApprovalResponse;
import com.lawbackend2.lawbackend2.entity.Approval;

import java.util.List;
import java.util.Map;

public interface ApprovalService {
    Long createApproval(ApprovalCreateRequest request, Long userId);
    PageResult<ApprovalResponse> getApprovalList(Integer pageNum, Integer pageSize, Long caseId, Long lawyerId, String approvalType, String approvalStatus, String status, String approvalTitle);
    ApprovalResponse getApprovalDetail(Long approvalId);
    void updateApproval(Long approvalId, ApprovalUpdateRequest request);
    void approveApproval(Long approvalId, ApprovalRequest request, Long approverId);
    void updateApprovalStatus(Long approvalId, ApprovalStatusRequest request);
    void deleteApproval(Long approvalId);
    Map<String, Object> getApprovalAttachments(Long approvalId, Boolean includeImages, Boolean includeFiles);
    List<ApprovalResponse> getApprovalProgressByCaseId(Long caseId, String approvalType);

    long getPendingCaseSubmitCount();
    long getPendingTaskCount();
    long getPendingTotalCount();
}