package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CaseProgressCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseProgressUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CaseProgress;
import com.lawbackend2.lawbackend2.service.CaseProgressService;
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
@RequestMapping("/case-progress")
@Tag(name = "案件进度管理", description = "案件进度管理相关接口")
public class CaseProgressController {

    @Autowired
    private CaseProgressService caseProgressService;

    @PostMapping
    @Operation(summary = "创建案件进度", description = "创建案件进度记录")
    public Result<CaseProgress> createProgress(@Valid @RequestBody CaseProgressCreateRequest request) {
        log.info("创建案件进度，请求参数：{}", request);

        try {
            Long userId = 1L;
            CaseProgress progress = caseProgressService.createProgress(request, userId);
            log.info("创建案件进度成功");
            return Result.success(progress);

        } catch (Exception e) {
            log.error("创建案件进度失败：{}", e.getMessage(), e);
            return Result.error("创建案件进度失败：" + e.getMessage());
        }
    }

    @GetMapping("/{progressId}")
    @Operation(summary = "获取案件进度详情", description = "根据进度ID获取案件进度详情")
    public Result<CaseProgress> getProgressById(
            @Parameter(description = "进度ID") @PathVariable Long progressId) {

        log.info("获取案件进度详情，进度ID：{}", progressId);

        try {
            CaseProgress progress = caseProgressService.getProgressById(progressId);
            log.info("获取案件进度详情成功");
            return Result.success(progress);

        } catch (Exception e) {
            log.error("获取案件进度详情失败：{}", e.getMessage(), e);
            return Result.error("获取案件进度详情失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取案件进度列表", description = "获取案件进度列表，支持按案件ID、进度阶段、状态等条件筛选")
    public Result<List<CaseProgress>> getProgressList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "进度阶段") @RequestParam(required = false) String progressStage,
            @Parameter(description = "进度状态") @RequestParam(required = false) String progressStatus,
            @Parameter(description = "是否完成") @RequestParam(required = false) Boolean isCompleted) {

        log.info("获取案件进度列表，page：{}，size：{}，caseId：{}，progressStage：{}，progressStatus：{}，isCompleted：{}",
                page, size, caseId, progressStage, progressStatus, isCompleted);

        try {
            List<CaseProgress> progressList = caseProgressService.getProgressList(page, size, caseId, progressStage, progressStatus, isCompleted);
            log.info("获取案件进度列表成功");
            return Result.success(progressList);

        } catch (Exception e) {
            log.error("获取案件进度列表失败：{}", e.getMessage(), e);
            return Result.error("获取案件进度列表失败：" + e.getMessage());
        }
    }

    @PutMapping("/{progressId}")
    @Operation(summary = "更新案件进度", description = "更新案件进度信息")
    public Result<CaseProgress> updateProgress(
            @Parameter(description = "进度ID") @PathVariable Long progressId,
            @Valid @RequestBody CaseProgressUpdateRequest request) {

        log.info("更新案件进度，进度ID：{}，请求参数：{}", progressId, request);

        try {
            Long userId = 1L;
            CaseProgress progress = caseProgressService.updateProgress(progressId, request, userId);
            log.info("更新案件进度成功");
            return Result.success(progress);

        } catch (Exception e) {
            log.error("更新案件进度失败：{}", e.getMessage(), e);
            return Result.error("更新案件进度失败：" + e.getMessage());
        }
    }

    @PostMapping("/{progressId}/complete")
    @Operation(summary = "完成案件进度", description = "标记案件进度为已完成")
    public Result<Void> completeProgress(
            @Parameter(description = "进度ID") @PathVariable Long progressId) {

        log.info("完成案件进度，进度ID：{}", progressId);

        try {
            Long userId = 1L;
            String userName = "测试用户";
            caseProgressService.completeProgress(progressId, userId, userName);
            log.info("完成案件进度成功");
            return Result.success();

        } catch (Exception e) {
            log.error("完成案件进度失败：{}", e.getMessage(), e);
            return Result.error("完成案件进度失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{progressId}")
    @Operation(summary = "删除案件进度", description = "删除案件进度记录")
    public Result<Void> deleteProgress(
            @Parameter(description = "进度ID") @PathVariable Long progressId) {

        log.info("删除案件进度，进度ID：{}", progressId);

        try {
            caseProgressService.deleteProgress(progressId);
            log.info("删除案件进度成功");
            return Result.success();

        } catch (Exception e) {
            log.error("删除案件进度失败：{}", e.getMessage(), e);
            return Result.error("删除案件进度失败：" + e.getMessage());
        }
    }

    @GetMapping("/case/{caseId}")
    @Operation(summary = "获取案件进度", description = "根据案件ID获取案件的所有进度记录")
    public Result<List<CaseProgress>> getProgressByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        log.info("获取案件进度，案件ID：{}", caseId);

        try {
            List<CaseProgress> progressList = caseProgressService.getProgressByCaseId(caseId);
            log.info("获取案件进度成功");
            return Result.success(progressList);

        } catch (Exception e) {
            log.error("获取案件进度失败：{}", e.getMessage(), e);
            return Result.error("获取案件进度失败：" + e.getMessage());
        }
    }

    @GetMapping("/case/{caseId}/in-progress")
    @Operation(summary = "获取进行中的案件进度", description = "根据案件ID获取进行中的案件进度")
    public Result<List<CaseProgress>> getInProgressProgressByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        log.info("获取进行中的案件进度，案件ID：{}", caseId);

        try {
            List<CaseProgress> progressList = caseProgressService.getInProgressProgressByCaseId(caseId);
            log.info("获取进行中的案件进度成功");
            return Result.success(progressList);

        } catch (Exception e) {
            log.error("获取进行中的案件进度失败：{}", e.getMessage(), e);
            return Result.error("获取进行中的案件进度失败：" + e.getMessage());
        }
    }

    @GetMapping("/case/{caseId}/completed")
    @Operation(summary = "获取已完成的案件进度", description = "根据案件ID获取已完成的案件进度")
    public Result<List<CaseProgress>> getCompletedProgressByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        log.info("获取已完成的案件进度，案件ID：{}", caseId);

        try {
            List<CaseProgress> progressList = caseProgressService.getCompletedProgressByCaseId(caseId);
            log.info("获取已完成的案件进度成功");
            return Result.success(progressList);

        } catch (Exception e) {
            log.error("获取已完成的案件进度失败：{}", e.getMessage(), e);
            return Result.error("获取已完成的案件进度失败：" + e.getMessage());
        }
    }

    @GetMapping("/case/{caseId}/overall-percentage")
    @Operation(summary = "获取案件整体进度百分比", description = "根据案件ID获取案件的整体进度百分比")
    public Result<Double> getOverallProgressPercentage(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {

        log.info("获取案件整体进度百分比，案件ID：{}", caseId);

        try {
            Double percentage = caseProgressService.getOverallProgressPercentage(caseId);
            log.info("获取案件整体进度百分比成功");
            return Result.success(percentage);

        } catch (Exception e) {
            log.error("获取案件整体进度百分比失败：{}", e.getMessage(), e);
            return Result.error("获取案件整体进度百分比失败：" + e.getMessage());
        }
    }
}
