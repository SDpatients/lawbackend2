package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundOperationLog;

public interface FundOperationLogService {

    Long createFundOperationLog(FundOperationLogCreateRequest request);

    PageResult<FundOperationLog> getFundOperationLogList(Integer pageNum, Integer pageSize, Long caseId, String operationType, String status);

    FundOperationLog getFundOperationLogDetail(Long logId);

    void updateFundOperationLog(Long logId, FundOperationLogUpdateRequest request);

    void updateFundOperationLogStatus(Long logId, FundOperationLogStatusRequest request);

    void deleteFundOperationLog(Long logId);
}
