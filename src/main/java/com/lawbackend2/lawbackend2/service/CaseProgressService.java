package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CaseProgressCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseProgressUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseProgress;

import java.util.List;

public interface CaseProgressService {
    CaseProgress createProgress(CaseProgressCreateRequest request, Long userId);

    CaseProgress getProgressById(Long progressId);

    List<CaseProgress> getProgressList(Integer page, Integer size, Long caseId, String progressStage, String progressStatus, Boolean isCompleted);

    CaseProgress updateProgress(Long progressId, CaseProgressUpdateRequest request, Long userId);

    void completeProgress(Long progressId, Long userId, String userName);

    void deleteProgress(Long progressId);

    List<CaseProgress> getProgressByCaseId(Long caseId);

    List<CaseProgress> getInProgressProgressByCaseId(Long caseId);

    List<CaseProgress> getCompletedProgressByCaseId(Long caseId);

    Double getOverallProgressPercentage(Long caseId);
}
