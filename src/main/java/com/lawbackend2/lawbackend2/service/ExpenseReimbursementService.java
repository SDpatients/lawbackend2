package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementItemCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ExpenseReimbursementUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ExpenseReimbursementResponse;
import com.lawbackend2.lawbackend2.entity.ExpenseReimbursementAttachment;

public interface ExpenseReimbursementService {

    Long createExpenseReimbursement(ExpenseReimbursementCreateRequest request, Long userId);

    ExpenseReimbursementResponse getExpenseReimbursementDetail(Long reimbursementId);

    PageResult<ExpenseReimbursementResponse> getExpenseReimbursementList(Integer pageNum, Integer pageSize, Long caseId, Long applicantId, String approvalStatus, java.time.LocalDate reimbursementDate);

    void updateExpenseReimbursement(ExpenseReimbursementUpdateRequest request);

    void deleteExpenseReimbursement(Long reimbursementId);

    void approveExpenseReimbursement(Long reimbursementId, ExpenseReimbursementApprovalRequest request, Long approverId);

    Long addExpenseReimbursementItem(ExpenseReimbursementItemCreateRequest request);

    void deleteExpenseReimbursementItem(Long itemId);

    Long uploadExpenseReimbursementAttachment(Long reimbursementId, String fileName, String filePath, Long fileSize, String fileType);

    void deleteExpenseReimbursementAttachment(Long attachmentId);
    
    ExpenseReimbursementAttachment getAttachmentById(Long attachmentId);

    Long linkAttachment(Long reimbursementId, Long fileId);
}
