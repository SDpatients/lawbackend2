package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.AnnouncementViewRecordCreateRequest;
import com.lawbackend2.lawbackend2.entity.AnnouncementViewRecord;
import com.lawbackend2.lawbackend2.service.AnnouncementViewRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/announcement-view-record")
@Tag(name = "公告查看记录", description = "公告查看记录相关接口")
public class AnnouncementViewRecordController {

    @Autowired
    private AnnouncementViewRecordService viewRecordService;

    @PostMapping
    @Operation(summary = "创建公告查看记录", description = "创建公告查看记录，同时更新公告的查看次数")
    public Result<AnnouncementViewRecord> createViewRecord(@Valid @RequestBody AnnouncementViewRecordCreateRequest request) {
        log.info("创建公告查看记录，请求参数：{}", request);

        try {
            AnnouncementViewRecord record = viewRecordService.createViewRecord(request);
            log.info("创建公告查看记录成功");
            return Result.success(record);

        } catch (Exception e) {
            log.error("创建公告查看记录失败：{}", e.getMessage(), e);
            return Result.error("创建公告查看记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "获取公告查看记录详情", description = "根据记录ID获取公告查看记录详情")
    public Result<AnnouncementViewRecord> getViewRecordById(
            @Parameter(description = "记录ID") @PathVariable Long recordId) {

        log.info("获取公告查看记录详情，记录ID：{}", recordId);

        try {
            AnnouncementViewRecord record = viewRecordService.getViewRecordById(recordId);
            log.info("获取公告查看记录详情成功");
            return Result.success(record);

        } catch (Exception e) {
            log.error("获取公告查看记录详情失败：{}", e.getMessage(), e);
            return Result.error("获取公告查看记录详情失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取公告查看记录列表", description = "获取公告查看记录列表，支持按公告ID、案件ID、查看人ID筛选")
    public Result<List<AnnouncementViewRecord>> getViewRecordList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "公告ID") @RequestParam(required = false) Long announcementId,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "查看人ID") @RequestParam(required = false) Long viewerId) {

        log.info("获取公告查看记录列表，page：{}，size：{}，announcementId：{}，caseId：{}，viewerId：{}",
                page, size, announcementId, caseId, viewerId);

        try {
            List<AnnouncementViewRecord> records = viewRecordService.getViewRecordList(page, size, announcementId, caseId, viewerId);
            log.info("获取公告查看记录列表成功");
            return Result.success(records);

        } catch (Exception e) {
            log.error("获取公告查看记录列表失败：{}", e.getMessage(), e);
            return Result.error("获取公告查看记录列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/count/announcement/{announcementId}")
    @Operation(summary = "获取公告查看次数", description = "根据公告ID获取公告的查看次数")
    public Result<Long> getViewCountByAnnouncementId(
            @Parameter(description = "公告ID") @PathVariable Long announcementId) {

        log.info("获取公告查看次数，公告ID：{}", announcementId);

        try {
            Long count = viewRecordService.getViewCountByAnnouncementId(announcementId);
            log.info("获取公告查看次数成功");
            return Result.success(count);

        } catch (Exception e) {
            log.error("获取公告查看次数失败：{}", e.getMessage(), e);
            return Result.error("获取公告查看次数失败：" + e.getMessage());
        }
    }

    @GetMapping("/count/case/{caseId}")
    @Operation(summary = "获取案件公告查看次数", description = "根据案件ID获取案件所有公告的查看次数")
    public Result<Long> getViewCountByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        log.info("获取案件公告查看次数，案件ID：{}", caseId);

        try {
            Long count = viewRecordService.getViewCountByCaseId(caseId);
            log.info("获取案件公告查看次数成功");
            return Result.success(count);

        } catch (Exception e) {
            log.error("获取案件公告查看次数失败：{}", e.getMessage(), e);
            return Result.error("获取案件公告查看次数失败：" + e.getMessage());
        }
    }

    @GetMapping("/count/viewer/{viewerId}")
    @Operation(summary = "获取用户查看次数", description = "根据用户ID获取用户查看公告的次数")
    public Result<Long> getViewCountByViewerId(
            @Parameter(description = "查看人ID") @PathVariable Long viewerId) {

        log.info("获取用户查看次数，用户ID：{}", viewerId);

        try {
            Long count = viewRecordService.getViewCountByViewerId(viewerId);
            log.info("获取用户查看次数成功");
            return Result.success(count);

        } catch (Exception e) {
            log.error("获取用户查看次数失败：{}", e.getMessage(), e);
            return Result.error("获取用户查看次数失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "删除公告查看记录", description = "根据记录ID删除公告查看记录")
    public Result<Void> deleteViewRecord(
            @Parameter(description = "记录ID") @PathVariable Long recordId) {

        log.info("删除公告查看记录，记录ID：{}", recordId);

        try {
            viewRecordService.deleteViewRecord(recordId);
            log.info("删除公告查看记录成功");
            return Result.success();

        } catch (Exception e) {
            log.error("删除公告查看记录失败：{}", e.getMessage(), e);
            return Result.error("删除公告查看记录失败：" + e.getMessage());
        }
    }
}
