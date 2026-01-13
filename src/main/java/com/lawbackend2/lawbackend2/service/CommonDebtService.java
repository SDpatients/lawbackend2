package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtRepaymentRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CommonDebt;

public interface CommonDebtService {

    Long createCommonDebt(CommonDebtCreateRequest request);

    PageResult<CommonDebt> getCommonDebtList(Integer pageNum, Integer pageSize, Long caseId, String debtType, String approvalStatus, String repaymentStatus);

    CommonDebt getCommonDebtDetail(Long debtId);

    void updateCommonDebt(Long debtId, CommonDebtUpdateRequest request);

    void approveCommonDebt(Long debtId, CommonDebtApprovalRequest request);

    void repayCommonDebt(Long debtId, CommonDebtRepaymentRequest request);

    void deleteCommonDebt(Long debtId);
}