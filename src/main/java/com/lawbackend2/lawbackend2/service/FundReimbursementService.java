package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementPaymentRequest;
import com.lawbackend2.lawbackend2.entity.FundReimbursement;

public interface FundReimbursementService {

    Long createFundReimbursement(FundReimbursementCreateRequest request);

    PageResult<FundReimbursement> getFundReimbursementList(Integer pageNum, Integer pageSize, Long caseId, String reimbursementType, Long applicantId, String approvalStatus, String paymentStatus);

    FundReimbursement getFundReimbursementDetail(Long reimbursementId);

    void approveFundReimbursement(Long reimbursementId, FundReimbursementApprovalRequest request);

    void payFundReimbursement(Long reimbursementId, FundReimbursementPaymentRequest request);

    void deleteFundReimbursement(Long reimbursementId);
}