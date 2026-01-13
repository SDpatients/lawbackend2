package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.EscrowManagementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.EscrowManagementReleaseRequest;
import com.lawbackend2.lawbackend2.entity.EscrowManagement;

public interface EscrowManagementService {

    Long createEscrowManagement(EscrowManagementCreateRequest request);

    PageResult<EscrowManagement> getEscrowManagementList(Integer pageNum, Integer pageSize, Long caseId, String escrowType, String releaseStatus);

    EscrowManagement getEscrowManagementDetail(Long escrowId);

    void releaseEscrow(Long escrowId, EscrowManagementReleaseRequest request);

    void deleteEscrowManagement(Long escrowId);
}