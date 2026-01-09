package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.BankruptCase;

import java.util.List;

public interface BankruptCaseService {
    BankruptCase createCase(CaseCreateRequest request, Long userId);

    BankruptCase getCaseById(Long caseId);

    List<BankruptCase> getCaseList(Integer pageNum, Integer pageSize, String caseStatus, String caseProgress);

    Long getCaseCount(String caseStatus, String caseProgress);

    BankruptCase updateCase(Long caseId, CaseUpdateRequest request);

    void updateCaseStatus(Long caseId, CaseStatusUpdateRequest request);

    void updateCaseProgress(Long caseId, CaseProgressUpdateRequest request);

    void reviewCase(Long caseId, CaseReviewRequest request, Long userId);

    BankruptCase getReviewStatus(Long caseId);

    List<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> getCaseSimpleList(Integer page, Integer size, String caseNumber);

    Long getCaseSimpleCount(String caseNumber);
}
