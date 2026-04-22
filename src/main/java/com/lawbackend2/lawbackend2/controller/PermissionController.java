package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.annotation.RateLimit;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.CreatePermissionRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdatePermissionRequest;
import com.lawbackend2.lawbackend2.dto.response.PermissionListResponse;
import com.lawbackend2.lawbackend2.dto.response.PermissionResponse;
import com.lawbackend2.lawbackend2.dto.response.PermissionTreeResponse;
import com.lawbackend2.lawbackend2.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permissions")
@Tag(name = "权限管理", description = "权限CRUD操作API")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "创建权限", description = "创建新权限，需要管理员权限")
    @AuditLog(module = "permission", moduleName = "权限管理", operationType = "CREATE", operationName = "创建权限")
    public Result<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        log.info("创建权限请求 - 权限代码: {}", request.getPermCode());
        PermissionResponse response = permissionService.createPermission(request);
        return Result.success(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:permission:query')")
    @RateLimit(limit = 50, timeout = 60)
    @Operation(summary = "获取权限列表", description = "分页查询权限列表，支持排序和筛选")
    public Result<PermissionListResponse> getPermissionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "sortOrder") String sortField,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "ASC") String sortOrder,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "权限状态") @RequestParam(required = false) String status) {
        
        PermissionListResponse response = permissionService.getPermissionList(page, size, sortField, sortOrder, keyword, status);
        return Result.success(response);
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:query')")
    @Operation(summary = "获取权限树", description = "获取权限树形结构")
    public Result<List<PermissionTreeResponse>> getPermissionTree() {
        log.info("获取权限树");
        List<PermissionTreeResponse> response = permissionService.getPermissionTree();
        return Result.success(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:query')")
    @Operation(summary = "获取单个权限", description = "根据权限ID查询权限详情")
    public Result<PermissionResponse> getPermissionById(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        log.info("查询权限详情 - 权限ID: {}", id);
        PermissionResponse response = permissionService.getPermissionById(id);
        return Result.success(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:edit')")
    @Operation(summary = "更新权限", description = "全量更新权限信息，需要管理员权限")
    @AuditLog(module = "permission", moduleName = "权限管理", operationType = "UPDATE", operationName = "更新权限")
    public Result<PermissionResponse> updatePermission(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @Valid @RequestBody UpdatePermissionRequest request) {
        log.info("更新权限信息 - 权限ID: {}", id);
        PermissionResponse response = permissionService.updatePermission(id, request);
        return Result.success(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "删除权限", description = "逻辑删除权限，需要管理员权限")
    @AuditLog(module = "permission", moduleName = "权限管理", operationType = "DELETE", operationName = "删除权限")
    public Result<Void> deletePermission(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        log.info("删除权限 - 权限ID: {}", id);
        permissionService.deletePermission(id);
        return Result.success();
    }
}
