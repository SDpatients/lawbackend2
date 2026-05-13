package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageRequest;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.entity.AuditLog;
import com.lawbackend2.lawbackend2.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/system/audit-log")
@RequiredArgsConstructor
@Tag(name = "审计日志管理", description = "系统审计日志相关接口")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/list")
    @Operation(summary = "查询审计日志列表", description = "分页查询审计日志列表")
    @PreAuthorize("hasAuthority('system:audit:list')")
    public Result<PageResult<AuditLog>> list(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            PageRequest pageRequest) {
        
        Page<AuditLog> page = auditLogService.search(
                userId, module, operationType, status, 
                startTime, endTime, keyword,
                org.springframework.data.domain.PageRequest.of(
                        pageRequest.getPage() - 1,
                        pageRequest.getSize()
                )
        );
        
        PageResult<AuditLog> result = new PageResult<>();
        result.setList(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setPage(pageRequest.getPage());
        result.setSize(pageRequest.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询审计日志详情", description = "根据ID查询审计日志详情")
    @PreAuthorize("hasAuthority('system:audit:list')")
    public Result<AuditLog> detail(
            @Parameter(description = "审计日志ID") @PathVariable Long id) {
        AuditLog auditLog = auditLogService.getById(id);
        return auditLog != null ? Result.success(auditLog) : Result.error("审计日志不存在");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户操作日志", description = "查询指定用户的操作日志")
    @PreAuthorize("hasAuthority('system:audit:list')")
    public Result<PageResult<AuditLog>> listByUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            PageRequest pageRequest) {
        
        Page<AuditLog> page = auditLogService.getByUserId(
                userId,
                org.springframework.data.domain.PageRequest.of(
                        pageRequest.getPage() - 1,
                        pageRequest.getSize()
                )
        );
        
        PageResult<AuditLog> result = new PageResult<>();
        result.setList(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setPage(pageRequest.getPage());
        result.setSize(pageRequest.getSize());
        return Result.success(result);
    }

    @GetMapping("/business")
    @Operation(summary = "查询业务操作日志", description = "查询指定业务对象的操作日志")
    @PreAuthorize("hasAuthority('system:audit:list')")
    public Result<PageResult<AuditLog>> listByBusiness(
            @Parameter(description = "业务类型") @RequestParam String businessType,
            @Parameter(description = "业务ID") @RequestParam Long businessId,
            PageRequest pageRequest) {
        
        Page<AuditLog> page = auditLogService.getByBusiness(
                businessType, businessId,
                org.springframework.data.domain.PageRequest.of(
                        pageRequest.getPage() - 1,
                        pageRequest.getSize()
                )
        );
        
        PageResult<AuditLog> result = new PageResult<>();
        result.setList(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setPage(pageRequest.getPage());
        result.setSize(pageRequest.getSize());
        return Result.success(result);
    }

    @GetMapping("/statistics/module")
    @Operation(summary = "按模块统计", description = "按模块统计操作数量")
    @PreAuthorize("hasAuthority('system:audit:list')")
    public Result<Map<String, Long>> statisticsByModule(
            @Parameter(description = "开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        if (startTime == null) startTime = LocalDateTime.now().minusDays(7);
        if (endTime == null) endTime = LocalDateTime.now();
        
        return Result.success(auditLogService.countGroupByModule(startTime, endTime));
    }

    @GetMapping("/statistics/operation")
    @Operation(summary = "按操作类型统计", description = "按操作类型统计数量")
    @PreAuthorize("hasAuthority('system:audit:list')")
    public Result<Map<String, Long>> statisticsByOperation(
            @Parameter(description = "开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        if (startTime == null) startTime = LocalDateTime.now().minusDays(7);
        if (endTime == null) endTime = LocalDateTime.now();
        
        return Result.success(auditLogService.countGroupByOperationType(startTime, endTime));
    }

    @GetMapping("/statistics/trend")
    @Operation(summary = "操作趋势统计", description = "按日期统计操作趋势")
    @PreAuthorize("hasAuthority('system:audit:list')")
    public Result<List<Map<String, Object>>> statisticsTrend(
            @Parameter(description = "开始时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        if (startTime == null) startTime = LocalDateTime.now().minusDays(30);
        if (endTime == null) endTime = LocalDateTime.now();
        
        return Result.success(auditLogService.getTrendData(startTime, endTime));
    }
}
