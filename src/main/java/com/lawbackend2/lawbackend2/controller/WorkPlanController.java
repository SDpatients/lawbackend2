package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanResponse;
import com.lawbackend2.lawbackend2.entity.WorkPlan;
import com.lawbackend2.lawbackend2.service.WorkPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "工作计划管理")
@RestController
@RequestMapping("/work-plan")
@Validated
public class WorkPlanController {

    private final WorkPlanService workPlanService;

    public WorkPlanController(WorkPlanService workPlanService) {
        this.workPlanService = workPlanService;
    }

    @Operation(summary = "创建工作计划")
    @PostMapping
    @AuditLog(module = "work-plan", moduleName = "工作计划管理", operationType = "CREATE", operationName = "创建工作计划")
    public Result<Map<String, Object>> createWorkPlan(@Valid @RequestBody WorkPlanCreateRequest request) {
        Long userId = getCurrentUserId();
        Long planId = workPlanService.createWorkPlan(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("planId", planId);

        return Result.success(data);
    }

    @Operation(summary = "工作计划列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<WorkPlanResponse>> getWorkPlanList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "计划类型") @RequestParam(required = false) String planType,
            @Parameter(description = "执行状态") @RequestParam(required = false) String executionStatus,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        Long userId = getCurrentUserId();
        PageResult<WorkPlanResponse> result = workPlanService.getWorkPlanList(pageNum, pageSize, caseId, planType, executionStatus, status, userId);
        return Result.success(result);
    }

    @Operation(summary = "获取工作计划详情")
    @GetMapping("/{planId}")
    public Result<WorkPlanResponse> getWorkPlanDetail(@Parameter(description = "计划ID") @PathVariable Long planId) {
        Long userId = getCurrentUserId();
        WorkPlanResponse response = workPlanService.getWorkPlanDetailResponse(planId, userId);
        return Result.success(response);
    }

    @Operation(summary = "更新工作计划")
    @PutMapping("/{planId}")
    @AuditLog(module = "work-plan", moduleName = "工作计划管理", operationType = "UPDATE", operationName = "更新工作计划")
    public Result<Void> updateWorkPlan(
            @Parameter(description = "计划ID") @PathVariable Long planId,
            @Valid @RequestBody WorkPlanUpdateRequest request) {

        Long userId = getCurrentUserId();
        workPlanService.updateWorkPlan(planId, request, userId);
        return Result.success();
    }

    @Operation(summary = "更新工作计划执行状态")
    @PutMapping("/{planId}/execution-status")
    public Result<Void> updateWorkPlanStatus(
            @Parameter(description = "计划ID") @PathVariable Long planId,
            @Valid @RequestBody WorkPlanStatusRequest request) {

        Long userId = getCurrentUserId();
        workPlanService.updateWorkPlanStatus(planId, request, userId);
        return Result.success();
    }

    @Operation(summary = "删除工作计划")
    @DeleteMapping("/{planId}")
    @AuditLog(module = "work-plan", moduleName = "工作计划管理", operationType = "DELETE", operationName = "删除工作计划")
    public Result<Void> deleteWorkPlan(@Parameter(description = "计划ID") @PathVariable Long planId) {
        workPlanService.deleteWorkPlan(planId);
        return Result.success();
    }

    @Operation(summary = "根据时间区间查询工作计划")
    @GetMapping("/list-by-time")
    public Result<PageResult<WorkPlanResponse>> getWorkPlanListByTimeRange(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "开始时间") @RequestParam String startDate,
            @Parameter(description = "结束时间") @RequestParam String endDate) {

        Long userId = getCurrentUserId();
        PageResult<WorkPlanResponse> result = workPlanService.getWorkPlanListByTimeRange(pageNum, pageSize, startDate, endDate, userId);
        return Result.success(result);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户ID");
    }
}
