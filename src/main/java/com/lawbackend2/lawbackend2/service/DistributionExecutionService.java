package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionCreateRequest;
import com.lawbackend2.lawbackend2.entity.DistributionExecution;

public interface DistributionExecutionService {

    Long createDistributionExecution(DistributionExecutionCreateRequest request);

    PageResult<DistributionExecution> getDistributionExecutionList(Integer pageNum, Integer pageSize, Long caseId, String distributionBatch, String approvalStatus, String executionStatus);

    DistributionExecution getDistributionExecutionDetail(Long executionId);

    void approveDistributionExecution(Long executionId, DistributionExecutionApprovalRequest request);

    void executeDistribution(Long executionId);

    void deleteDistributionExecution(Long executionId);
}