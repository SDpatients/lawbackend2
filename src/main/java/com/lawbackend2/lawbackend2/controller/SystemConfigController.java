package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.entity.SystemConfig;
import com.lawbackend2.lawbackend2.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/config")
@Tag(name = "系统配置管理", description = "系统配置管理相关接口")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    @Operation(summary = "获取所有系统配置", description = "获取所有激活状态的系统配置")
    public ApiResponse<List<SystemConfig>> getAllConfigs(
            @Parameter(description = "配置分组") @RequestParam(required = false) String configGroup) {
        List<SystemConfig> configs;
        if (configGroup != null && !configGroup.isEmpty()) {
            configs = systemConfigService.getConfigsByGroup(configGroup);
        } else {
            configs = systemConfigService.getAllConfigs();
        }
        return ApiResponse.success(configs);
    }

    @GetMapping("/{configKey}")
    @Operation(summary = "获取系统配置", description = "根据配置键获取系统配置")
    public ApiResponse<SystemConfig> getConfigByKey(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        SystemConfig config = systemConfigService.getConfigByKey(configKey);
        if (config == null) {
            return ApiResponse.error(404, "配置不存在");
        }
        return ApiResponse.success(config);
    }

    @PostMapping
    @Operation(summary = "创建系统配置", description = "创建新的系统配置")
    public ApiResponse<SystemConfig> createConfig(@Validated @RequestBody SystemConfig config) {
        SystemConfig createdConfig = systemConfigService.createConfig(config);
        return ApiResponse.success(createdConfig);
    }

    @PutMapping("/{configKey}")
    @Operation(summary = "更新系统配置", description = "根据配置键更新系统配置")
    public ApiResponse<SystemConfig> updateConfig(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Parameter(description = "配置值") @RequestParam String configValue) {
        SystemConfig updatedConfig = systemConfigService.updateConfig(configKey, configValue);
        return ApiResponse.success(updatedConfig);
    }

    @PutMapping("/{configId}/status")
    @Operation(summary = "更新配置状态", description = "更新系统配置的状态")
    public ApiResponse<Void> updateConfigStatus(
            @Parameter(description = "配置ID") @PathVariable Long configId,
            @Parameter(description = "状态") @RequestParam String status) {
        systemConfigService.updateConfigStatus(configId, status);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "删除系统配置", description = "删除系统配置（软删除）")
    public ApiResponse<Void> deleteConfig(
            @Parameter(description = "配置ID") @PathVariable Long configId) {
        systemConfigService.deleteConfig(configId);
        return ApiResponse.success(null);
    }
}
