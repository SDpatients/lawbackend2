package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionBatchUpdateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionReviewRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseSubmissionSummaryResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskSubmissionResponse;
import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CaseTaskSubmissionService {

    CaseTaskSubmissionResponse createSubmission(CaseTaskSubmissionCreateRequest request, Long userId);

    Page<CaseTaskSubmissionResponse> getSubmissionsByTaskId(Long caseTaskId, Pageable pageable);

    CaseTaskSubmissionResponse getSubmissionById(Long submissionId);

    CaseTaskSubmissionResponse reviewSubmission(Long submissionId, CaseTaskSubmissionReviewRequest request, Long reviewerId);

    void deleteSubmission(Long submissionId, Long userId);

    List<CaseTaskSubmissionResponse> getLatestSubmissions(Long caseTaskId, Integer limit);

    CaseTaskSubmissionResponse updateSubmission(CaseTaskSubmissionUpdateRequest request, Long userId);

    CaseTaskSubmissionResponse batchUpdateSubmission(CaseTaskSubmissionBatchUpdateRequest request, Long userId);

    CaseSubmissionSummaryResponse getCaseSubmissionSummary(Long caseId);

    java.util.Map<Long, java.util.List<CaseTaskSubmissionResponse>> getLatestSubmissionsBatch(java.util.List<Long> caseTaskIds);
}
