package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundApproval;

public interface FundApprovalService {

    Long createFundApproval(FundApprovalCreateRequest request);

    PageResult<FundApproval> getFundApprovalList(Integer pageNum, Integer pageSize, Long caseId, String approvalStatus, String status);

    FundApproval getFundApprovalDetail(Long approvalId);

    void updateFundApproval(Long approvalId, FundApprovalUpdateRequest request);

    void approveFundApproval(Long approvalId, FundApprovalRequest request);

    void updateFundApprovalStatus(Long approvalId, FundApprovalStatusRequest request);

    void deleteFundApproval(Long approvalId);
}
