package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.BankruptCase;

import java.util.List;

public interface BankruptCaseService {
    BankruptCase createCase(CaseCreateRequest request, Long userId);

    BankruptCase getCaseById(Long caseId);

    List<BankruptCase> getCaseList(Integer pageNum, Integer pageSize, String caseStatus, String caseProgress, String keyword);

    Long getCaseCount(String caseStatus, String caseProgress, String keyword);

    BankruptCase updateCase(Long caseId, CaseUpdateRequest request);

    void updateCaseStatus(Long caseId, CaseStatusUpdateRequest request);

    void updateCaseProgress(Long caseId, CaseProgressUpdateRequest request);
    
    void updateCaseProgress(Long caseId, String caseProgress);

    void reviewCase(Long caseId, CaseReviewRequest request, Long userId);

    BankruptCase getReviewStatus(Long caseId);

    void submitForReview(Long caseId, Long userId);

    void withdrawReview(Long caseId, Long userId);

    void resubmitForReview(Long caseId, Long userId);

    void batchReview(CaseBatchReviewRequest request, Long userId);

    void revokeReview(Long caseId, Long userId);

    List<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> getCaseSimpleList(Long userId, Integer page, Integer size, String caseNumber);

    Long getCaseSimpleCount(Long userId, String caseNumber);

    List<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> getCaseSimpleInfoByCaseNumber(Integer page, Integer size, String caseNumber);

    Long countByCaseNumberLike(String caseNumber);

    List<BankruptCase> getUserCaseList(Long userId, Integer pageNum, Integer pageSize, String caseStatus, String keyword, String caseProgress);

    Long getUserCaseCount(Long userId, String caseStatus, String keyword, String caseProgress);

    List<BankruptCase> getCasesByReviewStatus(String reviewStatus, Integer pageNum, Integer pageSize, String keyword);

    Long getCasesCountByReviewStatus(String reviewStatus, String keyword);

    List<BankruptCase> getCasesByReviewerId(Long reviewerId, Integer pageNum, Integer pageSize, String reviewStatus);

    Long getCasesCountByReviewerId(Long reviewerId, String reviewStatus);

    List<Object[]> getReviewStatusStatistics();

    com.lawbackend2.lawbackend2.dto.MyCaseStatisticsResponse getMyCaseStatistics(Long userId);

    void deleteCase(Long caseId);

    com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse getCaseRelatedData(Long caseId);

    void archiveCase(Long caseId, Long userId);

    void unarchiveCase(Long caseId, Long userId);

    List<com.lawbackend2.lawbackend2.dto.response.CaseAccessibleUserResponse> getAccessibleUsers(Long caseId);
}
