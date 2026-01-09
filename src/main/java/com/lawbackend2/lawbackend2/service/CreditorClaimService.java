package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CreditorClaimCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimReviewRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorClaim;

import java.util.List;

public interface CreditorClaimService {
    CreditorClaim createClaim(CreditorClaimCreateRequest request, Long userId);

    CreditorClaim getClaimById(Long claimId);

    List<CreditorClaim> getClaimList(Integer pageNum, Integer pageSize, Long caseId, String registrationStatus);

    Long getClaimCount(Long caseId, String registrationStatus);

    CreditorClaim updateClaim(Long claimId, CreditorClaimUpdateRequest request);

    void reviewClaim(Long claimId, CreditorClaimReviewRequest request, Long userId);

    CreditorClaim getClaimReviewStatus(Long claimId);
}
