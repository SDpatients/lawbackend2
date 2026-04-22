package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.annotation.RateLimit;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.AssignRolePermissionsRequest;
import com.lawbackend2.lawbackend2.dto.request.CreateRoleRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdateRoleRequest;
import com.lawbackend2.lawbackend2.dto.response.RoleListResponse;
import com.lawbackend2.lawbackend2.dto.response.RoleResponse;
import com.lawbackend2.lawbackend2.service.RoleService;
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
@RequestMapping("/roles")
@Tag(name = "角色管理", description = "角色CRUD操作API")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "创建角色", description = "创建新角色，需要管理员权限")
    @AuditLog(module = "role", moduleName = "角色管理", operationType = "CREATE", operationName = "创建角色")
    public Result<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        log.info("创建角色请求 - 角色代码: {}", request.getRoleCode());
        RoleResponse response = roleService.createRole(request);
        return Result.success(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:query')")
    @RateLimit(limit = 50, timeout = 60)
    @Operation(summary = "获取角色列表", description = "分页查询角色列表，支持排序和筛选")
    public Result<RoleListResponse> getRoleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "sortOrder") String sortField,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "ASC") String sortOrder,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "角色状态") @RequestParam(required = false) String status) {
        
        RoleListResponse response = roleService.getRoleList(page, size, sortField, sortOrder, keyword, status);
        return Result.success(response);
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('system:role:query')")
    @Operation(summary = "获取单个角色", description = "根据角色ID查询角色详情")
    public Result<RoleResponse> getRoleById(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        log.info("查询角色详情 - 角色ID: {}", id);
        RoleResponse response = roleService.getRoleById(id);
        return Result.success(response);
    }

    @PutMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    @Operation(summary = "更新角色", description = "全量更新角色信息，需要管理员权限")
    @AuditLog(module = "role", moduleName = "角色管理", operationType = "UPDATE", operationName = "更新角色")
    public Result<RoleResponse> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        log.info("更新角色信息 - 角色ID: {}", id);
        RoleResponse response = roleService.updateRole(id, request);
        return Result.success(response);
    }

    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "删除角色", description = "逻辑删除角色，需要管理员权限")
    @AuditLog(module = "role", moduleName = "角色管理", operationType = "DELETE", operationName = "删除角色")
    public Result<Void> deleteRole(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        log.info("删除角色 - 角色ID: {}", id);
        roleService.deleteRole(id);
        return Result.success();
    }

    @PostMapping("/{roleId:\\d+}/permissions")
    @PreAuthorize("hasAuthority('system:role:assign')")
    @RateLimit(limit = 20, timeout = 60)
    @Operation(summary = "为角色分配权限", description = "为角色分配权限，需要管理员权限")
    @AuditLog(module = "role", moduleName = "角色管理", operationType = "ASSIGN", operationName = "为角色分配权限")
    public Result<Void> assignPermissionsToRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Valid @RequestBody AssignRolePermissionsRequest request) {
        log.info("为角色分配权限 - 角色ID: {}, 权限数量: {}", roleId, request.getPermissionIds().size());
        roleService.assignPermissionsToRole(roleId, request.getPermissionIds());
        return Result.success();
    }

    @GetMapping("/{roleId:\\d+}/permissions")
    @PreAuthorize("hasAuthority('system:role:query')")
    @Operation(summary = "查询角色权限", description = "查询角色已分配的权限列表")
    public Result<List<Long>> getRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        log.info("查询角色权限 - 角色ID: {}", roleId);
        List<Long> permissionIds = roleService.getRolePermissions(roleId);
        return Result.success(permissionIds);
    }

    @DeleteMapping("/{roleId:\\d+}/permissions")
    @PreAuthorize("hasAuthority('system:role:assign')")
    @RateLimit(limit = 20, timeout = 60)
    @Operation(summary = "移除角色权限", description = "移除角色的指定权限，需要管理员权限")
    public Result<Void> removePermissionsFromRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @RequestBody AssignRolePermissionsRequest request) {
        log.info("移除角色权限 - 角色ID: {}, 移除权限数量: {}", roleId, request.getPermissionIds().size());
        roleService.removePermissionsFromRole(roleId, request.getPermissionIds());
        return Result.success();
    }

    @PutMapping("/{roleId:\\d+}/permissions")
    @PreAuthorize("hasAuthority('system:role:assign')")
    @RateLimit(limit = 20, timeout = 60)
    @Operation(summary = "更新角色权限", description = "更新角色的权限列表，需要管理员权限")
    public Result<Void> updateRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Valid @RequestBody AssignRolePermissionsRequest request) {
        log.info("更新角色权限 - 角色ID: {}, 权限数量: {}", roleId, request.getPermissionIds().size());
        roleService.updateRolePermissions(roleId, request.getPermissionIds());
        return Result.success();
    }
}
