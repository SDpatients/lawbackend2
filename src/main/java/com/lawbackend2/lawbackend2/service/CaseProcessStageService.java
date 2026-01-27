package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CaseProcessStageStatusUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseProcessStage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CaseProcessStageService {
    boolean saveStage(CaseProcessStage stage);

    boolean saveStageWithFiles(CaseProcessStage stage, List<MultipartFile> files);

    boolean updateStage(CaseProcessStage stage);

    boolean updateStageWithFiles(CaseProcessStage stage, List<MultipartFile> files);

    boolean removeStage(Long id);

    CaseProcessStage getStageById(Long id);

    List<CaseProcessStage> getStagesByCaseId(Long caseId);

    List<CaseProcessStage> getStagesByCaseIdAndStageNum(Long caseId, Integer stageNum);

    List<CaseProcessStage> getStagesByCaseIdAndModuleCode(Long caseId, String moduleCode);

    boolean updateStatusByCaseIdAndModuleCode(CaseProcessStageStatusUpdateRequest request);
}
