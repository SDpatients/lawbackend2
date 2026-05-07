package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.entity.SystemConfig;
import com.lawbackend2.lawbackend2.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system-param")
@Tag(name = "系统参数管理", description = "系统参数的CRUD管理")
public class SystemParamController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/list")
    @Operation(summary = "获取系统参数列表(分页)", description = "支持按分组和状态筛选")
    public Result<PageResult<SystemConfig>> getSystemParamList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "配置分组") @RequestParam(required = false) String configGroup,
            @Parameter(description = "状态(ACTIVE/INACTIVE)") @RequestParam(required = false) String status) {
        List<SystemConfig> all;
        if (configGroup != null && !configGroup.isEmpty()) {
            all = systemConfigService.getConfigsByGroup(configGroup);
        } else {
            all = systemConfigService.getAllConfigs();
        }

        Long total = (long) all.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, all.size());
        List<SystemConfig> pageList = fromIndex < all.size() ? all.subList(fromIndex, toIndex) : List.of();

        return Result.success(PageResult.of(total, pageList, pageNum, pageSize));
    }

    @GetMapping("/{configKey}")
    @Operation(summary = "根据配置键获取系统参数")
    public Result<SystemConfig> getSystemParamByKey(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        SystemConfig config = systemConfigService.getConfigByKey(configKey);
        if (config == null) {
            return Result.error(404, "系统参数不存在");
        }
        return Result.success(config);
    }

    @PostMapping
    @Operation(summary = "创建系统参数")
    @AuditLog(module = "system-param", moduleName = "系统参数管理", operationType = "CREATE", operationName = "创建系统参数")
    public Result<SystemConfig> createSystemParam(@RequestBody SystemConfig config) {
        SystemConfig created = systemConfigService.createConfig(config);
        return Result.success(created);
    }

    @PutMapping("/{configKey}")
    @Operation(summary = "更新系统参数值")
    @AuditLog(module = "system-param", moduleName = "系统参数管理", operationType = "UPDATE", operationName = "更新系统参数")
    public Result<SystemConfig> updateSystemParam(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Parameter(description = "配置值") @RequestParam String configValue) {
        SystemConfig updated = systemConfigService.updateConfig(configKey, configValue);
        return Result.success(updated);
    }

    @PutMapping("/{configId}/status")
    @Operation(summary = "更新系统参数状态")
    public Result<Void> updateSystemParamStatus(
            @Parameter(description = "配置ID") @PathVariable Long configId,
            @Parameter(description = "状态(ACTIVE/INACTIVE)") @RequestParam String status) {
        systemConfigService.updateConfigStatus(configId, status);
        return Result.success();
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "删除系统参数(软删除)")
    @AuditLog(module = "system-param", moduleName = "系统参数管理", operationType = "DELETE", operationName = "删除系统参数")
    public Result<Void> deleteSystemParam(
            @Parameter(description = "配置ID") @PathVariable Long configId) {
        systemConfigService.deleteConfig(configId);
        return Result.success();
    }
}
