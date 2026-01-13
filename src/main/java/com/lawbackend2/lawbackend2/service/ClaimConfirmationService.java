package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ClaimConfirmationCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimConfirmationUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;

import java.util.List;

public interface ClaimConfirmationService {
    ClaimConfirmation createConfirmation(ClaimConfirmationCreateRequest request, Long userId);

    ClaimConfirmation getConfirmationById(Long confirmationId);

    ClaimConfirmation getConfirmationByClaimId(Long claimRegistrationId);

    List<ClaimConfirmation> getConfirmationListByCaseId(Long caseId, Integer pageNum, Integer pageSize);

    ClaimConfirmation updateConfirmation(Long confirmationId, ClaimConfirmationUpdateRequest request);

    void deleteConfirmation(Long confirmationId);

    void submitObjection(Long confirmationId, Long userId);

    void handleNegotiation(Long confirmationId, String result, Long userId);

    void submitCourtRuling(Long confirmationId, Long userId);

    void updateLawsuitStatus(Long confirmationId, String status, Long userId);

    void finalizeConfirmation(Long confirmationId, Long userId);

    List<ClaimConfirmation> getObjectionsByCaseId(Long caseId);

    List<ClaimConfirmation> getLawsuitsByCaseId(Long caseId);

    List<ClaimConfirmation> getPendingConfirmations(Long caseId);

    Long getConfirmationCount(Long caseId, String confirmationStatus);
}
