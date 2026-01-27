package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.FileRecordInfo;
import com.lawbackend2.lawbackend2.dto.request.BatchFilesRequest;
import com.lawbackend2.lawbackend2.dto.request.BatchSubmissionsRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionBatchUpdateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionReviewRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionUpdateRequest;
import com.lawbackend2.lawbackend2.dto.request.FileSortOrderUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseSubmissionSummaryResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskSubmissionResponse;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.CaseTaskFileService;
import com.lawbackend2.lawbackend2.service.CaseTaskSubmissionService;
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

import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "案件任务提交管理")
@RestController
@RequestMapping("/api/case-task-submissions")
@Validated
public class CaseTaskSubmissionController {

    private final CaseTaskSubmissionService submissionService;
    private final CaseTaskFileService fileService;
    private final UserRepository userRepository;
    private final FileRecordRepository fileRecordRepository;

    public CaseTaskSubmissionController(CaseTaskSubmissionService submissionService,
                                       CaseTaskFileService fileService,
                                       UserRepository userRepository,
                                       FileRecordRepository fileRecordRepository) {
        this.submissionService = submissionService;
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.fileRecordRepository = fileRecordRepository;
    }

    private String getUserName(Long userId) {
        if (userId == null) {
            return "";
        }
        return userRepository.findById(userId)
                .map(User::getRealName)
                .orElse("");
    }

    @Operation(summary = "创建任务提交")
    @PostMapping
    public Result<Map<String, Object>> createSubmission(@Valid @RequestBody CaseTaskSubmissionCreateRequest request) {
        Long userId = 1L;
        CaseTaskSubmissionResponse submission = submissionService.createSubmission(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("submissionId", submission.getId());
        data.put("submissionNumber", submission.getSubmissionNumber());

        return Result.success(data);
    }

    @Operation(summary = "查询任务提交列表")
    @GetMapping
    public Result<Page<CaseTaskSubmissionResponse>> getSubmissions(
            @Parameter(description = "任务ID") @RequestParam Long caseTaskId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CaseTaskSubmissionResponse> result = submissionService.getSubmissionsByTaskId(caseTaskId, pageable);

        return Result.success(result);
    }

    @Operation(summary = "查询单个提交详情")
    @GetMapping("/{id}")
    public Result<CaseTaskSubmissionResponse> getSubmission(@Parameter(description = "提交ID") @PathVariable Long id) {
        CaseTaskSubmissionResponse response = submissionService.getSubmissionById(id);
        return Result.success(response);
    }

    @Operation(summary = "审核任务提交")
    @PutMapping("/{id}/review")
    public Result<CaseTaskSubmissionResponse> reviewSubmission(
            @Parameter(description = "提交ID") @PathVariable Long id,
            @Valid @RequestBody CaseTaskSubmissionReviewRequest request) {

        Long reviewerId = 1L;
        CaseTaskSubmissionResponse submissionResponse = submissionService.reviewSubmission(id, request, reviewerId);
        return Result.success(submissionResponse);
    }

    @Operation(summary = "删除任务提交")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSubmission(@Parameter(description = "提交ID") @PathVariable Long id) {
        Long userId = 1L;
        submissionService.deleteSubmission(id, userId);
        return Result.success();
    }

    @Operation(summary = "查询最新提交记录")
    @GetMapping("/latest")
    public Result<List<CaseTaskSubmissionResponse>> getLatestSubmissions(
            @Parameter(description = "任务ID") @RequestParam Long caseTaskId,
            @Parameter(description = "返回数量") @RequestParam(required = false) Integer limit) {

        List<CaseTaskSubmissionResponse> result = submissionService.getLatestSubmissions(caseTaskId, limit);
        return Result.success(result);
    }

    @Operation(summary = "上传任务提交文件")
    @PostMapping("/{submissionId}/files")
    public Result<Map<String, Object>> uploadFile(
            @Parameter(description = "提交ID") @PathVariable Long submissionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder) {

        Long userId = 1L;
        FileRecord fileRecord = fileService.uploadFileForSubmission(submissionId, file, description, userId, sortOrder);

        Map<String, Object> data = new HashMap<>();
        data.put("id", fileRecord.getId());
        data.put("originalFileName", fileRecord.getOriginalFileName());
        data.put("filePath", fileRecord.getFilePath());
        data.put("fileSize", fileRecord.getFileSize());
        data.put("uploadTime", fileRecord.getUploadTime());
        data.put("sortOrder", fileRecord.getSortOrder());

        return Result.success(data);
    }

    @Operation(summary = "查询任务提交文件列表")
    @GetMapping("/{submissionId}/files")
    public Result<List<FileRecordInfo>> getSubmissionFiles(@Parameter(description = "提交ID") @PathVariable Long submissionId) {
        Page<FileRecord> page = fileRecordRepository.findByConditions(
                "CASE_TASK_SUBMISSION",
                submissionId.toString(),
                "ACTIVE",
                org.springframework.data.domain.PageRequest.of(0, 1000, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "sortOrder"))
        );

        List<FileRecordInfo> result = page.getContent().stream().map(file -> {
            FileRecordInfo info = new FileRecordInfo();
            info.setId(file.getId());
            info.setOriginalFileName(file.getOriginalFileName());
            info.setFilePath(file.getFilePath());
            info.setFileSize(file.getFileSize());
            info.setUploadTime(file.getUploadTime());
            info.setSortOrder(file.getSortOrder());

            User user = userRepository.findById(file.getUploadUserId()).orElse(null);
            info.setUploadUserName(user != null ? user.getRealName() : "");

            return info;
        }).collect(java.util.stream.Collectors.toList());

        return Result.success(result);
    }

    @Operation(summary = "删除任务提交文件")
    @DeleteMapping("/{submissionId}/files/{fileId}")
    public Result<Void> deleteSubmissionFile(
            @Parameter(description = "提交ID") @PathVariable Long submissionId,
            @Parameter(description = "文件ID") @PathVariable Long fileId) {

        Long userId = 1L;
        FileRecord file = fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new com.lawbackend2.lawbackend2.exception.BusinessException("文件不存在"));

        if (!"CASE_TASK_SUBMISSION".equals(file.getBizType()) || !submissionId.toString().equals(file.getBizId())) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件不属于该提交");
        }

        file.setIsDeleted(true);
        file.setDeleteTime(java.time.LocalDateTime.now());
        file.setDeleteUserId(userId);
        fileRecordRepository.save(file);

        return Result.success();
    }

    @Operation(summary = "更新任务提交")
    @PutMapping
    public Result<CaseTaskSubmissionResponse> updateSubmission(@Valid @RequestBody CaseTaskSubmissionUpdateRequest request) {
        Long userId = 1L;
        CaseTaskSubmissionResponse response = submissionService.updateSubmission(request, userId);
        return Result.success(response);
    }

    @Operation(summary = "更新任务提交文件描述")
    @PutMapping("/{submissionId}/files/{fileId}")
    public Result<Void> updateFileDescription(
            @Parameter(description = "提交ID") @PathVariable Long submissionId,
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @RequestParam(required = false) String description) {

        FileRecord file = fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new com.lawbackend2.lawbackend2.exception.BusinessException("文件不存在"));

        if (!"CASE_TASK_SUBMISSION".equals(file.getBizType()) || !submissionId.toString().equals(file.getBizId())) {
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件不属于该提交");
        }

        file.setDescription(description);
        fileRecordRepository.save(file);

        return Result.success();
    }

    @Operation(summary = "批量更新任务提交")
    @PutMapping("/batch")
    public Result<CaseTaskSubmissionResponse> batchUpdateSubmission(@Valid @RequestBody CaseTaskSubmissionBatchUpdateRequest request) {
        Long userId = 1L;
        CaseTaskSubmissionResponse response = submissionService.batchUpdateSubmission(request, userId);
        return Result.success(response);
    }

    @Operation(summary = "批量更新文件排序")
    @PutMapping("/{submissionId}/files/sort-order")
    public Result<Void> updateFilesSortOrder(
            @Parameter(description = "提交ID") @PathVariable Long submissionId,
            @Valid @RequestBody FileSortOrderUpdateRequest request) {

        for (FileSortOrderUpdateRequest.FileSortOrderItem item : request.getFiles()) {
            FileRecord file = fileRecordRepository.findById(item.getFileId())
                    .orElseThrow(() -> new com.lawbackend2.lawbackend2.exception.BusinessException("文件不存在: " + item.getFileId()));

            if (!"CASE_TASK_SUBMISSION".equals(file.getBizType()) || !submissionId.toString().equals(file.getBizId())) {
                throw new com.lawbackend2.lawbackend2.exception.BusinessException("文件不属于该提交: " + item.getFileId());
            }

            file.setSortOrder(item.getSortOrder());
            fileRecordRepository.save(file);
        }

        return Result.success();
    }

    @Operation(summary = "查询案件提交汇总")
    @GetMapping("/case-summary/{caseId}")
    public Result<CaseSubmissionSummaryResponse> getCaseSubmissionSummary(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        CaseSubmissionSummaryResponse response = submissionService.getCaseSubmissionSummary(caseId);
        return Result.success(response);
    }

    @Operation(summary = "批量获取任务提交记录")
    @PostMapping("/latest/batch")
    public Result<java.util.Map<Long, java.util.List<CaseTaskSubmissionResponse>>> getLatestSubmissionsBatch(
            @Valid @RequestBody BatchSubmissionsRequest request) {

        java.util.Map<Long, java.util.List<CaseTaskSubmissionResponse>> result = 
            submissionService.getLatestSubmissionsBatch(request.getCaseTaskIds());
        return Result.success(result);
    }

    @Operation(summary = "批量获取提交文件")
    @PostMapping("/files/batch")
    public Result<java.util.Map<Long, java.util.List<FileRecordInfo>>> getSubmissionFilesBatch(
            @Valid @RequestBody BatchFilesRequest request) {

        java.util.List<String> bizIds = request.getSubmissionIds().stream()
            .map(String::valueOf)
            .collect(java.util.stream.Collectors.toList());

        java.util.List<FileRecord> files = fileRecordRepository.findByBizTypeAndBizIds(
            "CASE_TASK_SUBMISSION", 
            bizIds, 
            "ACTIVE"
        );

        java.util.Map<Long, java.util.List<FileRecordInfo>> result = new java.util.HashMap<>();

        for (Long submissionId : request.getSubmissionIds()) {
            result.put(submissionId, new java.util.ArrayList<>());
        }

        for (FileRecord file : files) {
            Long submissionId = Long.parseLong(file.getBizId());
            java.util.List<FileRecordInfo> fileList = result.get(submissionId);
            if (fileList != null) {
                FileRecordInfo info = new FileRecordInfo();
                info.setId(file.getId());
                info.setOriginalFileName(file.getOriginalFileName());
                info.setFilePath(file.getFilePath());
                info.setFileSize(file.getFileSize());
                info.setUploadTime(file.getUploadTime());
                info.setSortOrder(file.getSortOrder());

                User user = userRepository.findById(file.getUploadUserId()).orElse(null);
                info.setUploadUserName(user != null ? user.getRealName() : "");

                fileList.add(info);
            }
        }

        return Result.success(result);
    }
}
