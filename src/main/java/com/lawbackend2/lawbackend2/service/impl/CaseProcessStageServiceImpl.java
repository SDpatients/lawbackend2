package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.entity.CaseProcessStage;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CaseProcessStageRepository;
import com.lawbackend2.lawbackend2.service.CaseProcessStageService;
import com.lawbackend2.lawbackend2.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CaseProcessStageServiceImpl implements CaseProcessStageService {

    private final CaseProcessStageRepository caseProcessStageRepository;
    private final FileService fileService;
    private final ObjectMapper objectMapper;

    public CaseProcessStageServiceImpl(CaseProcessStageRepository caseProcessStageRepository, 
                                    FileService fileService,
                                    ObjectMapper objectMapper) {
        this.caseProcessStageRepository = caseProcessStageRepository;
        this.fileService = fileService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStage(CaseProcessStage stage) {
        try {
            caseProcessStageRepository.save(stage);
            log.info("保存阶段数据成功, caseId: {}, stageNum: {}, moduleCode: {}", 
                     stage.getCaseId(), stage.getStageNum(), stage.getModuleCode());
            return true;
        } catch (Exception e) {
            log.error("保存阶段数据失败", e);
            throw new BusinessException("保存阶段数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStage(CaseProcessStage stage) {
        try {
            if (stage.getId() == null) {
                throw new BusinessException("阶段数据ID不能为空");
            }
            CaseProcessStage existingStage = caseProcessStageRepository.findById(stage.getId())
                    .orElseThrow(() -> new BusinessException("阶段数据不存在"));
            
            caseProcessStageRepository.save(stage);
            log.info("更新阶段数据成功, id: {}, caseId: {}", stage.getId(), stage.getCaseId());
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新阶段数据失败", e);
            throw new BusinessException("更新阶段数据失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeStage(Long id) {
        try {
            CaseProcessStage stage = caseProcessStageRepository.findById(id)
                    .orElseThrow(() -> new BusinessException("阶段数据不存在"));
            
            stage.setIsDeleted(true);
            caseProcessStageRepository.save(stage);
            log.info("删除阶段数据成功, id: {}", id);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除阶段数据失败", e);
            throw new BusinessException("删除阶段数据失败: " + e.getMessage());
        }
    }

    @Override
    public CaseProcessStage getStageById(Long id) {
        return caseProcessStageRepository.findById(id)
                .orElseThrow(() -> new BusinessException("阶段数据不存在"));
    }

    @Override
    public List<CaseProcessStage> getStagesByCaseId(Long caseId) {
        return caseProcessStageRepository.findByCaseIdAndNotDeleted(caseId);
    }

    @Override
    public List<CaseProcessStage> getStagesByCaseIdAndStageNum(Long caseId, Integer stageNum) {
        return caseProcessStageRepository.findByCaseIdAndStageNumAndNotDeleted(caseId, stageNum);
    }

    @Override
    public List<CaseProcessStage> getStagesByCaseIdAndModuleCode(Long caseId, String moduleCode) {
        return caseProcessStageRepository.findByCaseIdAndModuleCodeAndNotDeleted(caseId, moduleCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStageWithFiles(CaseProcessStage stage, List<MultipartFile> files) {
        try {
            String bizId = stage.getCaseId() + "_" + stage.getStageNum() + "_" + stage.getModuleCode();
            
            if (files != null && !files.isEmpty()) {
                List<FileRecord> uploadedFiles = new ArrayList<>();
                for (MultipartFile file : files) {
                    FileRecord fileRecord = fileService.uploadFile(file, "CASE_TASK", bizId);
                    uploadedFiles.add(fileRecord);
                }
                
                List<Map<String, Object>> attachmentsList = uploadedFiles.stream()
                        .map(this::convertToFileMap)
                        .collect(Collectors.toList());
                stage.setAttachments(objectMapper.writeValueAsString(attachmentsList));
            }
            
            caseProcessStageRepository.save(stage);
            log.info("保存阶段数据及文件成功, caseId: {}, stageNum: {}, moduleCode: {}, fileCount: {}", 
                     stage.getCaseId(), stage.getStageNum(), stage.getModuleCode(), 
                     files != null ? files.size() : 0);
            return true;
        } catch (Exception e) {
            log.error("保存阶段数据及文件失败", e);
            throw new BusinessException("保存阶段数据及文件失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStageWithFiles(CaseProcessStage stage, List<MultipartFile> files) {
        try {
            if (stage.getId() == null) {
                throw new BusinessException("阶段数据ID不能为空");
            }
            CaseProcessStage existingStage = caseProcessStageRepository.findById(stage.getId())
                    .orElseThrow(() -> new BusinessException("阶段数据不存在"));
            
            String bizId = existingStage.getCaseId() + "_" + existingStage.getStageNum() + "_" + existingStage.getModuleCode();
            
            if (files != null && !files.isEmpty()) {
                List<FileRecord> uploadedFiles = new ArrayList<>();
                for (MultipartFile file : files) {
                    FileRecord fileRecord = fileService.uploadFile(file, "CASE_TASK", bizId);
                    uploadedFiles.add(fileRecord);
                }
                
                List<Map<String, Object>> attachmentsList = uploadedFiles.stream()
                        .map(this::convertToFileMap)
                        .collect(Collectors.toList());
                
                if (existingStage.getAttachments() != null && !existingStage.getAttachments().isEmpty()) {
                    List<Map<String, Object>> existingAttachments = objectMapper.readValue(
                            existingStage.getAttachments(), 
                            new TypeReference<List<Map<String, Object>>>() {});
                    attachmentsList.addAll(existingAttachments);
                }
                
                stage.setAttachments(objectMapper.writeValueAsString(attachmentsList));
            }
            
            caseProcessStageRepository.save(stage);
            log.info("更新阶段数据及文件成功, id: {}, caseId: {}, fileCount: {}", 
                     stage.getId(), stage.getCaseId(), files != null ? files.size() : 0);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新阶段数据及文件失败", e);
            throw new BusinessException("更新阶段数据及文件失败: " + e.getMessage());
        }
    }

    private Map<String, Object> convertToFileMap(FileRecord fileRecord) {
        Map<String, Object> fileMap = new java.util.HashMap<>();
        fileMap.put("fileId", fileRecord.getId());
        fileMap.put("fileName", fileRecord.getOriginalFileName());
        fileMap.put("filePath", fileRecord.getFilePath());
        fileMap.put("fileType", fileRecord.getFileExtension());
        fileMap.put("fileSize", fileRecord.getFileSize());
        fileMap.put("uploadTime", fileRecord.getUploadTime());
        return fileMap;
    }
}
