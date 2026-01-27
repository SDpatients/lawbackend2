package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.FileRecordInfo;
import com.lawbackend2.lawbackend2.dto.request.BatchUpdateStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.BatchUpdateResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskDetailResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskStatistics;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.CaseTaskFileService;
import com.lawbackend2.lawbackend2.service.CaseTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "案件任务管理")
@RestController
@RequestMapping("/api/case-tasks")
@Validated
public class CaseTaskController {

    private final CaseTaskService caseTaskService;
    private final CaseTaskFileService caseTaskFileService;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final FileRecordRepository fileRecordRepository;

    public CaseTaskController(CaseTaskService caseTaskService,
                             CaseTaskFileService caseTaskFileService,
                             BankruptCaseRepository bankruptCaseRepository,
                             FileRecordRepository fileRecordRepository) {
        this.caseTaskService = caseTaskService;
        this.caseTaskFileService = caseTaskFileService;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.fileRecordRepository = fileRecordRepository;
    }

    @Operation(summary = "查询案件任务列表")
    @GetMapping
    public Result<Page<CaseTaskResponse>> getTasks(
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            @Parameter(description = "任务编号") @RequestParam(required = false) String taskCode,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CaseTaskResponse> result = caseTaskService.getTasksByCaseId(caseId, status, taskCode, pageable);
        return Result.success(result);
    }

    @Operation(summary = "查询单个任务详情")
    @GetMapping("/{id}")
    public Result<CaseTaskDetailResponse> getTask(@Parameter(description = "任务ID") @PathVariable Long id) {
        CaseTask task = caseTaskService.getTaskById(id);

        CaseTaskDetailResponse response = new CaseTaskDetailResponse();
        response.setId(task.getId());
        response.setCaseId(task.getCaseId());
        response.setTaskCode(task.getTaskCode());
        response.setTaskName(task.getTaskName());
        response.setTaskDescription(task.getTaskDescription());
        response.setStatus(task.getStatus());
        response.setCreateTime(task.getCreateTime());
        response.setUpdateTime(task.getUpdateTime());

        BankruptCase bankruptCase = bankruptCaseRepository.findById(task.getCaseId()).orElse(null);
        if (bankruptCase != null) {
            response.setCaseNumber(bankruptCase.getCaseNumber());
        }

        List<FileRecordInfo> files = caseTaskFileService.getFilesByTaskId(id);
        response.setFiles(files);

        return Result.success(response);
    }

    @Operation(summary = "更新任务信息")
    @PatchMapping("/{id}")
    public Result<CaseTaskResponse> updateTask(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestBody CaseTaskUpdateRequest request) {

        CaseTaskResponse response = caseTaskService.updateTask(id, request);
        return Result.success(response);
    }

    @Operation(summary = "批量更新任务状态")
    @PutMapping("/batch-status")
    public Result<BatchUpdateResponse> batchUpdateStatus(@RequestBody BatchUpdateStatusRequest request) {
        caseTaskService.batchUpdateTaskStatus(request.getTaskIds(), request.getStatus());

        BatchUpdateResponse response = new BatchUpdateResponse();
        response.setSuccessCount(request.getTaskIds().size());
        response.setFailCount(0);

        return Result.success(response);
    }

    @Operation(summary = "案件任务统计")
    @GetMapping("/statistics/{caseId}")
    public Result<CaseTaskStatistics> getStatistics(@Parameter(description = "案件ID") @PathVariable Long caseId) {
        CaseTaskStatistics statistics = caseTaskService.getStatistics(caseId);
        return Result.success(statistics);
    }

    @Operation(summary = "上传任务文件")
    @PostMapping("/{taskId}/files")
    public Result<Map<String, Object>> uploadFile(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description) {

        Long userId = 1L;
        FileRecord fileRecord = caseTaskFileService.uploadFile(taskId, file, description, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("id", fileRecord.getId());
        data.put("originalFileName", fileRecord.getOriginalFileName());
        data.put("filePath", fileRecord.getFilePath());
        data.put("fileSize", fileRecord.getFileSize());
        data.put("uploadTime", fileRecord.getUploadTime());

        return Result.success(data);
    }

    @Operation(summary = "查询任务文件列表")
    @GetMapping("/{taskId}/files")
    public Result<List<FileRecordInfo>> getFiles(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        List<FileRecordInfo> files = caseTaskFileService.getFilesByTaskId(taskId);
        return Result.success(files);
    }

    @Operation(summary = "删除任务文件")
    @DeleteMapping("/{taskId}/files/{fileId}")
    public Result<Void> deleteFile(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            @Parameter(description = "文件ID") @PathVariable Long fileId) {

        Long userId = 1L;
        caseTaskFileService.deleteFile(taskId, fileId, userId);
        return Result.success();
    }
}
