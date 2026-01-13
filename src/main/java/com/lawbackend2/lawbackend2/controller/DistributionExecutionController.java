package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionCreateRequest;
import com.lawbackend2.lawbackend2.entity.DistributionExecution;
import com.lawbackend2.lawbackend2.service.DistributionExecutionService;
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
@Tag(name = "分配执行管理")
@RestController
@RequestMapping("/distribution-execution")
@Validated
public class DistributionExecutionController {

    private final DistributionExecutionService distributionExecutionService;

    public DistributionExecutionController(DistributionExecutionService distributionExecutionService) {
        this.distributionExecutionService = distributionExecutionService;
    }

    @Operation(summary = "创建分配执行")
    @PostMapping
    public Result<Map<String, Object>> createDistributionExecution(@Valid @RequestBody DistributionExecutionCreateRequest request) {
        Long executionId = distributionExecutionService.createDistributionExecution(request);

        Map<String, Object> data = new HashMap<>();
        data.put("executionId", executionId);

        return Result.success(data);
    }

    @Operation(summary = "分配执行列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<DistributionExecution>> getDistributionExecutionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "分配批次") @RequestParam(required = false) String distributionBatch,
            @Parameter(description = "审批状态") @RequestParam(required = false) String approvalStatus,
            @Parameter(description = "执行状态") @RequestParam(required = false) String executionStatus) {

        PageResult<DistributionExecution> result = distributionExecutionService.getDistributionExecutionList(pageNum, pageSize, caseId, distributionBatch, approvalStatus, executionStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取分配执行详情")
    @GetMapping("/{executionId}")
    public Result<DistributionExecution> getDistributionExecutionDetail(@Parameter(description = "执行ID") @PathVariable Long executionId) {
        DistributionExecution execution = distributionExecutionService.getDistributionExecutionDetail(executionId);
        return Result.success(execution);
    }

    @Operation(summary = "审批分配执行")
    @PutMapping("/{executionId}/approval")
    public Result<Void> approveDistributionExecution(
            @Parameter(description = "执行ID") @PathVariable Long executionId,
            @Valid @RequestBody DistributionExecutionApprovalRequest request) {

        distributionExecutionService.approveDistributionExecution(executionId, request);
        return Result.success();
    }

    @Operation(summary = "执行分配")
    @PutMapping("/{executionId}/execute")
    public Result<Void> executeDistribution(@Parameter(description = "执行ID") @PathVariable Long executionId) {
        distributionExecutionService.executeDistribution(executionId);
        return Result.success();
    }

    @Operation(summary = "删除分配执行")
    @DeleteMapping("/{executionId}")
    public Result<Void> deleteDistributionExecution(@Parameter(description = "执行ID") @PathVariable Long executionId) {
        distributionExecutionService.deleteDistributionExecution(executionId);
        return Result.success();
    }
}