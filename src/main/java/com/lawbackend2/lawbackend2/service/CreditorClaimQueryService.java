package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.CreditorClaimQueryRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimQueryResponse;

public interface CreditorClaimQueryService {
    PageResult<CreditorClaimQueryResponse> queryClaims(CreditorClaimQueryRequest request);
}
