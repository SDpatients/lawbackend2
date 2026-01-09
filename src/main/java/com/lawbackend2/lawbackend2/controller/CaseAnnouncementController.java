package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementPublishRequest;
import com.lawbackend2.lawbackend2.dto.CaseAnnouncementUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import com.lawbackend2.lawbackend2.service.CaseAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    public CaseAnnouncementController(CaseAnnouncementService caseAnnouncementService) {
        this.caseAnnouncementService = caseAnnouncementService;
    }

    @Operation(summary = "创建案件公告")
    @PostMapping
    public Result<Map<String, Object>> createAnnouncement(@Valid @RequestBody CaseAnnouncementCreateRequest request) {
        Long userId = getCurrentUserId();
        CaseAnnouncement announcement = caseAnnouncementService.createAnnouncement(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("announcementId", announcement.getId());

        log.info("创建案件公告成功, ID: {}", announcement.getId());
        return Result.success(data);
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

    @Operation(summary = "删除案件公告")
    @DeleteMapping("/{announcementId}")
    public Result<Void> deleteAnnouncement(
            @Parameter(description = "公告ID") @PathVariable Long announcementId) {

        caseAnnouncementService.deleteAnnouncement(announcementId);
        log.info("删除案件公告成功, ID: {}", announcementId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
