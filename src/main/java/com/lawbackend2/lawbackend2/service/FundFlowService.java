package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundFlowCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundFlow;

public interface FundFlowService {

    Long createFundFlow(FundFlowCreateRequest request);

    PageResult<com.lawbackend2.lawbackend2.dto.response.FundFlowResponse> getFundFlowList(Integer pageNum, Integer pageSize, Long caseId, Long fundAccountId, String flowType, String status);

    FundFlow getFundFlowDetail(Long flowId);

    void updateFundFlow(Long flowId, FundFlowUpdateRequest request);

    void updateFundFlowStatus(Long flowId, FundFlowStatusRequest request);

    void deleteFundFlow(Long flowId);
}
