package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.WorkLogCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogCreateWithFilesRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkLogUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.WorkLogDetailResponse;
import com.lawbackend2.lawbackend2.entity.WorkLog;
import com.lawbackend2.lawbackend2.service.WorkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "工作日志")
@RestController
@RequestMapping("/work-log")
@Validated
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @Operation(summary = "创建工作日志")
    @PostMapping
    @AuditLog(module = "work-log", moduleName = "工作日志", operationType = "CREATE", operationName = "创建工作日志")
    public Result<Map<String, Object>> createWorkLog(@Valid @RequestBody WorkLogCreateRequest request) {
        Long logId = workLogService.createWorkLog(request);

        Map<String, Object> data = new HashMap<>();
        data.put("logId", logId);

        return Result.success(data);
    }

    @Operation(summary = "创建工作日志并上传文件")
    @PostMapping("/with-files")
    public Result<Map<String, Object>> createWorkLogWithFiles(
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId,
            @Parameter(description = "工作日期") @RequestParam("workDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate workDate,
            @Parameter(description = "工作类型") @RequestParam("workType") String workType,
            @Parameter(description = "工作内容") @RequestParam("workContent") String workContent,
            @Parameter(description = "工作结果") @RequestParam(value = "workResult", required = false) String workResult,
            @Parameter(description = "备注") @RequestParam(value = "remark", required = false) String remark,
            @Parameter(description = "文件列表") @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        WorkLogCreateWithFilesRequest request = new WorkLogCreateWithFilesRequest();
        request.setCaseId(caseId);
        request.setWorkDate(workDate);
        request.setWorkType(workType);
        request.setWorkContent(workContent);
        request.setWorkResult(workResult);
        request.setRemark(remark);

        Map<String, Object> result = workLogService.createWorkLogWithFiles(request, files);
        return Result.success(result);
    }

    @Operation(summary = "工作日志列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<WorkLog>> getWorkLogList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "工作类型") @RequestParam(required = false) String workType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "创建者ID") @RequestParam(required = false) Long createUserId,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<WorkLog> result = workLogService.getWorkLogList(pageNum, pageSize, caseId, workType, startDate, endDate, createUserId, status);
        return Result.success(result);
    }

    @Operation(summary = "获取工作日志详情")
    @GetMapping("/{logId}")
    public Result<WorkLog> getWorkLogDetail(@Parameter(description = "日志ID") @PathVariable Long logId) {
        WorkLog workLog = workLogService.getWorkLogDetail(logId);
        return Result.success(workLog);
    }

    @Operation(summary = "获取工作日志详情(包含文件)")
    @GetMapping("/{logId}/with-files")
    public Result<WorkLogDetailResponse> getWorkLogDetailWithFiles(@Parameter(description = "日志ID") @PathVariable Long logId) {
        WorkLogDetailResponse response = workLogService.getWorkLogDetailWithFiles(logId);
        return Result.success(response);
    }

    @Operation(summary = "更新工作日志信息")
    @PutMapping("/{logId}")
    @AuditLog(module = "work-log", moduleName = "工作日志", operationType = "UPDATE", operationName = "更新工作日志信息")
    public Result<Void> updateWorkLog(
            @Parameter(description = "日志ID") @PathVariable Long logId,
            @Valid @RequestBody WorkLogUpdateRequest request) {

        workLogService.updateWorkLog(logId, request);
        return Result.success();
    }

    @Operation(summary = "更新工作日志并上传文件")
    @PutMapping("/{logId}/with-files")
    public Result<Map<String, Object>> updateWorkLogWithFiles(
            @Parameter(description = "日志ID") @PathVariable Long logId,
            @Parameter(description = "工作类型") @RequestParam(value = "workType", required = false) String workType,
            @Parameter(description = "工作内容") @RequestParam(value = "workContent", required = false) String workContent,
            @Parameter(description = "工作结果") @RequestParam(value = "workResult", required = false) String workResult,
            @Parameter(description = "备注") @RequestParam(value = "remark", required = false) String remark,
            @Parameter(description = "文件列表") @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        WorkLogUpdateRequest request = new WorkLogUpdateRequest();
        request.setWorkType(workType);
        request.setWorkContent(workContent);
        request.setWorkResult(workResult);
        request.setRemark(remark);

        Map<String, Object> result = workLogService.updateWorkLogWithFiles(logId, request, files);
        return Result.success(result);
    }

    @Operation(summary = "更新工作日志状态")
    @PutMapping("/{logId}/status")
    public Result<Void> updateWorkLogStatus(
            @Parameter(description = "日志ID") @PathVariable Long logId,
            @Valid @RequestBody WorkLogStatusRequest request) {

        workLogService.updateWorkLogStatus(logId, request);
        return Result.success();
    }

    @Operation(summary = "删除工作日志")
    @DeleteMapping("/{logId}")
    @AuditLog(module = "work-log", moduleName = "工作日志", operationType = "DELETE", operationName = "删除工作日志")
    public Result<Void> deleteWorkLog(@Parameter(description = "日志ID") @PathVariable Long logId) {
        workLogService.deleteWorkLog(logId);
        return Result.success();
    }

    @Operation(summary = "删除工作日志(级联删除关联文件)")
    @DeleteMapping("/{logId}/with-files")
    public Result<Void> deleteWorkLogWithFiles(@Parameter(description = "日志ID") @PathVariable Long logId) {
        workLogService.deleteWorkLogWithFiles(logId);
        return Result.success();
    }
}
