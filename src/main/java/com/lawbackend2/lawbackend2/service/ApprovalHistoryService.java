package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.response.ApprovalHistoryResponse;
import com.lawbackend2.lawbackend2.entity.ApprovalHistory;

public interface ApprovalHistoryService {
    PageResult<ApprovalHistoryResponse> getApprovalHistoryList(Integer pageNum, Integer pageSize, Long approvalId, Long caseId, Long approverId, String approvalType);
    ApprovalHistoryResponse getApprovalHistoryDetail(Long historyId);
    ApprovalHistory createApprovalHistory(ApprovalHistory approvalHistory);
    ApprovalHistory updateApprovalHistory(Long historyId, ApprovalHistory approvalHistory);
    void deleteApprovalHistory(Long historyId);
}