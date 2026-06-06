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
    @Operation(summary = "查询审计日志列表（增强版）", description = "支持多条件搜索的分页查询")
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
    public Result<PageResult<AuditLog>> list(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "完整性状态") @RequestParam(required = false) String integrityStatus,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序字段") @RequestParam(required = false, defaultValue = "createTime") String sortField,
            @Parameter(description = "排序方向（asc/desc）") @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            PageRequest pageRequest) {

        org.springframework.data.domain.Pageable pageable;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage() - 1,
                    pageRequest.getSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, sortField)
            );
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage() - 1,
                    pageRequest.getSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, sortField)
            );
        }

        Page<AuditLog> page = auditLogService.search(
                userId, module, operationType, status, integrityStatus,
                startTime, endTime, keyword,
                pageable
        );

        PageResult<AuditLog> result = new PageResult<>();
        result.setList(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setPage(pageRequest.getPage());
        result.setSize(pageRequest.getSize());
        return Result.success(result);
    }

    @GetMapping("/advanced-search")
    @Operation(summary = "高级搜索", description = "支持用户账号、业务ID、IP地址、请求URL的高级搜索")
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
    public Result<PageResult<AuditLog>> advancedSearch(
            @Parameter(description = "用户账号（模糊匹配）") @RequestParam(required = false) String userAccount,
            @Parameter(description = "业务ID") @RequestParam(required = false) Long businessId,
            @Parameter(description = "IP地址（模糊匹配）") @RequestParam(required = false) String ipAddress,
            @Parameter(description = "请求URL（模糊匹配）") @RequestParam(required = false) String requestUrl,
            @Parameter(description = "排序字段") @RequestParam(required = false, defaultValue = "createTime") String sortField,
            @Parameter(description = "排序方向（asc/desc）") @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            PageRequest pageRequest) {

        org.springframework.data.domain.Pageable pageable;
        if ("asc".equalsIgnoreCase(sortOrder)) {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage() - 1,
                    pageRequest.getSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, sortField)
            );
        } else {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage() - 1,
                    pageRequest.getSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, sortField)
            );
        }

        Page<AuditLog> page = auditLogService.advancedSearch(
                userAccount, businessId, ipAddress, requestUrl,
                pageable
        );

        PageResult<AuditLog> result = new PageResult<>();
        result.setList(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setPage(pageRequest.getPage());
        result.setSize(pageRequest.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询审计日志详情", description = "根据ID查询审计日志详情，包含哈希链信息")
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
    public Result<AuditLog> detail(
            @Parameter(description = "审计日志ID") @PathVariable Long id) {
        AuditLog auditLog = auditLogService.getById(id);
        return auditLog != null ? Result.success(auditLog) : Result.error("审计日志不存在");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户操作日志", description = "查询指定用户的操作日志")
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('system:audit:list', 'ADMIN')")
    public Result<List<Map<String, Object>>> statisticsTrend(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        if (startTime == null) startTime = LocalDateTime.now().minusDays(30);
        if (endTime == null) endTime = LocalDateTime.now();

        return Result.success(auditLogService.getTrendData(startTime, endTime));
    }

    @GetMapping("/integrity/verify/{id}")
    @Operation(summary = "验证单条审计日志完整性", description = "验证指定审计日志的哈希链完整性")
    @PreAuthorize("hasAnyAuthority('system:audit:integrity', 'ADMIN')")
    public Result<Boolean> verifyIntegrity(
            @Parameter(description = "审计日志ID") @PathVariable Long id) {
        boolean isValid = auditLogService.verifyIntegrity(id);
        return Result.success(isValid);
    }

    @PostMapping("/integrity/verify-all")
    @Operation(summary = "验证所有审计日志完整性", description = "验证所有审计日志的哈希链完整性，返回被篡改的日志列表")
    @PreAuthorize("hasAnyAuthority('system:audit:integrity', 'ADMIN')")
    public Result<List<AuditLog>> verifyAllIntegrity() {
        List<AuditLog> tamperedLogs = auditLogService.verifyAllIntegrity();
        return Result.success(tamperedLogs);
    }

    @GetMapping("/integrity/report")
    @Operation(summary = "获取完整性报告", description = "获取审计日志完整性统计报告")
    @PreAuthorize("hasAnyAuthority('system:audit:integrity', 'ADMIN')")
    public Result<Map<String, Object>> getIntegrityReport() {
        Map<String, Object> report = auditLogService.getIntegrityReport();
        return Result.success(report);
    }

    @PostMapping("/integrity/migrate")
    @Operation(summary = "迁移并生成哈希链", description = "使用 Java 代码重新为所有审计日志生成哈希链，确保与验证逻辑一致")
    @PreAuthorize("hasAnyAuthority('system:audit:integrity', 'ADMIN')")
    public Result<Map<String, Object>> migrateHashChain() {
        Map<String, Object> result = auditLogService.migrateAndGenerateHashChain();
        return Result.success(result);
    }
}
