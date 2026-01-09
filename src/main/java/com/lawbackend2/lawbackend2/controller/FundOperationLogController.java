package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundOperationLog;
import com.lawbackend2.lawbackend2.service.FundOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "资金操作日志")
@RestController
@RequestMapping("/fund-operation-log")
@Validated
public class FundOperationLogController {

    private final FundOperationLogService fundOperationLogService;

    public FundOperationLogController(FundOperationLogService fundOperationLogService) {
        this.fundOperationLogService = fundOperationLogService;
    }

    @Operation(summary = "创建资金操作日志")
    @PostMapping
    public Result<Map<String, Object>> createFundOperationLog(@Valid @RequestBody FundOperationLogCreateRequest request) {
        Long logId = fundOperationLogService.createFundOperationLog(request);

        Map<String, Object> data = new HashMap<>();
        data.put("logId", logId);

        return Result.success(data);
    }

    @Operation(summary = "资金操作日志列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<FundOperationLog>> getFundOperationLogList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<FundOperationLog> result = fundOperationLogService.getFundOperationLogList(pageNum, pageSize, caseId, operationType, status);
        return Result.success(result);
    }

    @Operation(summary = "获取资金操作日志详情")
    @GetMapping("/{logId}")
    public Result<FundOperationLog> getFundOperationLogDetail(@Parameter(description = "日志ID") @PathVariable Long logId) {
        FundOperationLog fundOperationLog = fundOperationLogService.getFundOperationLogDetail(logId);
        return Result.success(fundOperationLog);
    }

    @Operation(summary = "更新资金操作日志信息")
    @PutMapping("/{logId}")
    public Result<Void> updateFundOperationLog(
            @Parameter(description = "日志ID") @PathVariable Long logId,
            @Valid @RequestBody FundOperationLogUpdateRequest request) {

        fundOperationLogService.updateFundOperationLog(logId, request);
        return Result.success();
    }

    @Operation(summary = "更新资金操作日志状态")
    @PutMapping("/{logId}/status")
    public Result<Void> updateFundOperationLogStatus(
            @Parameter(description = "日志ID") @PathVariable Long logId,
            @Valid @RequestBody FundOperationLogStatusRequest request) {

        fundOperationLogService.updateFundOperationLogStatus(logId, request);
        return Result.success();
    }

    @Operation(summary = "删除资金操作日志")
    @DeleteMapping("/{logId}")
    public Result<Void> deleteFundOperationLog(@Parameter(description = "日志ID") @PathVariable Long logId) {
        fundOperationLogService.deleteFundOperationLog(logId);
        return Result.success();
    }
}
