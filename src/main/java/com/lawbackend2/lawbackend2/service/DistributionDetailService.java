package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DistributionDetailCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.DistributionDetailPaymentRequest;
import com.lawbackend2.lawbackend2.entity.DistributionDetail;

public interface DistributionDetailService {

    Long createDistributionDetail(DistributionDetailCreateRequest request);

    PageResult<DistributionDetail> getDistributionDetailList(Integer pageNum, Integer pageSize, Long distributionExecutionId, Long caseId, String creditorType, String paymentStatus);

    DistributionDetail getDistributionDetailDetail(Long detailId);

    void payDistributionDetail(Long detailId, DistributionDetailPaymentRequest request);

    void deleteDistributionDetail(Long detailId);
}