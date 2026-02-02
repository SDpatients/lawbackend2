package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStagesResponse;
import com.lawbackend2.lawbackend2.dto.CreditorCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorInfoResponse;
import com.lawbackend2.lawbackend2.dto.CreditorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;

import java.util.List;

public interface CreditorInfoService {
    CreditorInfo createCreditor(CreditorCreateRequest request, Long userId);

    CreditorInfo getCreditorById(Long creditorId);

    CreditorInfoResponse getCreditorByIdWithCaseInfo(Long creditorId, Long userId);

    List<CreditorInfo> getCreditorList(Integer pageNum, Integer pageSize, Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status);

    Long getCreditorCount(Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status);

    CreditorInfo updateCreditor(Long creditorId, CreditorUpdateRequest request);

    void deleteCreditor(Long creditorId);

    PageResult<CreditorInfoResponse> getCreditorListWithCaseInfo(Integer pageNum, Integer pageSize, Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status, Long userId);

    CreditorClaimStagesResponse getCreditorClaimStages(Long creditorId, Long userId);
}
