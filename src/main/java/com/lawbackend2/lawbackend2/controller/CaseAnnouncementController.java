package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementPublishRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementUpdateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseAnnouncementCreateWithFilesRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseAnnouncementWithFilesResponse;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.service.CaseAnnouncementService;
import com.lawbackend2.lawbackend2.service.FileService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "案件公告管理")
@RestController
@RequestMapping("/case-announcement")
@Validated
public class CaseAnnouncementController {

    private final CaseAnnouncementService caseAnnouncementService;
    private final FileService fileService;

    public CaseAnnouncementController(CaseAnnouncementService caseAnnouncementService, FileService fileService) {
        this.caseAnnouncementService = caseAnnouncementService;
        this.fileService = fileService;
    }

    @Operation(summary = "创建案件公告")
    @PostMapping
    public Result<Map<String, Object>> createAnnouncement(@Valid @RequestBody CaseAnnouncementCreateRequest request) {
        Long userId = getCurrentUserId();
        CaseAnnouncement announcement = caseAnnouncementService.createAnnouncement(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("announcementId", announcement.getId());

        log.info("创建案件公告成功，ID: {}", announcement.getId());
        return Result.success(data);
    }

    @Operation(summary = "创建案件公告（带文件上传）")
    @PostMapping("/with-files")
    public Result<CaseAnnouncementWithFilesResponse> createAnnouncementWithFiles(
            @Parameter(description = "公告数据") @ModelAttribute @Valid CaseAnnouncementCreateWithFilesRequest request) {
        Long userId = getCurrentUserId();
        CaseAnnouncementWithFilesResponse response = caseAnnouncementService.createAnnouncementWithFiles(request, userId);

        log.info("创建案件公告（带文件）成功，ID: {}, 文件数量：{}", response.getAnnouncementId(), response.getFiles().size());
        return Result.success(response);
    }

    @Operation(summary = "案件公告列表")
    @GetMapping("/list")
    public Result<PageResult<CaseAnnouncement>> getAnnouncementList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        List<CaseAnnouncement> list = caseAnnouncementService.getAnnouncementList(pageNum, pageSize, caseId, status);
        Long total = caseAnnouncementService.getAnnouncementCount(caseId, status);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/{announcementId}")
    public Result<CaseAnnouncement> getAnnouncementById(@Parameter(description = "公告ID") @PathVariable Long announcementId) {
        CaseAnnouncement announcement = caseAnnouncementService.getAnnouncementById(announcementId);
        return Result.success(announcement);
    }

    @Operation(summary = "更新案件公告")
    @PutMapping("/{announcementId}")
    public Result<Void> updateAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @Valid @RequestBody CaseAnnouncementUpdateRequest request) {

        caseAnnouncementService.updateAnnouncement(announcementId, request);
        log.info("更新案件公告成功, ID: {}", announcementId);
        return Result.success();
    }

    @Operation(summary = "发布公告")
    @PostMapping("/{announcementId}/publish")
    public Result<Void> publishAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @Valid @RequestBody CaseAnnouncementPublishRequest request) {

        Long userId = getCurrentUserId();
        caseAnnouncementService.publishAnnouncement(announcementId, request, userId);
        log.info("发布公告成功, ID: {}", announcementId);
        return Result.success();
    }

    @Operation(summary = "置顶公告")
    @PostMapping("/{announcementId}/top")
    public Result<Void> topAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @Valid @RequestBody CaseAnnouncementPublishRequest request) {

        Long userId = getCurrentUserId();
        caseAnnouncementService.topAnnouncement(announcementId, request, userId);
        log.info("置顶公告成功, ID: {}", announcementId);
        return Result.success();
    }

    @Operation(summary = "取消置顶公告")
    @DeleteMapping("/{announcementId}/top")
    public Result<Void> unTopAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId) {

        Long userId = getCurrentUserId();
        caseAnnouncementService.unTopAnnouncement(announcementId, userId);
        log.info("取消置顶公告成功, ID: {}", announcementId);
        return Result.success();
    }

    @Operation(summary = "删除案件公告")
    @DeleteMapping("/{announcementId}")
    public Result<Void> deleteAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId) {

        caseAnnouncementService.deleteAnnouncement(announcementId);
        log.info("删除案件公告成功, ID: {}", announcementId);
        return Result.success();
    }

    @Operation(summary = "获取公告附件列表")
    @GetMapping("/{announcementId}/attachments")
    public Result<List<FileRecord>> getAnnouncementAttachments(
            @Parameter(description = "公告ID") @PathVariable Long announcementId) {

        List<FileRecord> attachments = caseAnnouncementService.getAnnouncementAttachments(announcementId);
        return Result.success(attachments);
    }

    @Operation(summary = "上传公告附件")
    @PostMapping("/{announcementId}/attachments/upload")
    public Result<List<FileRecord>> uploadAnnouncementAttachments(
            @Parameter(description = "公告ID") @PathVariable Long announcementId,
            @Parameter(description = "文件列表") @RequestParam("files") List<MultipartFile> files) {

        caseAnnouncementService.getAnnouncementById(announcementId);
        List<FileRecord> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            FileRecord fileRecord = fileService.uploadFile(file, "announcement", String.valueOf(announcementId));
            uploadedFiles.add(fileRecord);
        }

        log.info("公告附件上传成功, 公告ID: {}, 文件数量: {}", announcementId, uploadedFiles.size());
        return Result.success(uploadedFiles);
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
