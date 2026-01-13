package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.RateLimit;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.AssignUserRolesRequest;
import com.lawbackend2.lawbackend2.dto.response.UserWithRolesResponse;
import com.lawbackend2.lawbackend2.service.UserRoleService;
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
@RequestMapping("/user-roles")
@Tag(name = "用户角色管理", description = "用户角色关联管理API")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:query')")
    @Operation(summary = "获取用户角色列表", description = "获取所有用户及其关联的角色信息")
    public Result<List<UserWithRolesResponse>> getUserRoleList() {
        log.info("获取用户角色列表");
        List<UserWithRolesResponse> userRoleList = userRoleService.getUserRoleList();
        return Result.success(userRoleList);
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('system:user:assign')")
    @RateLimit(limit = 20, timeout = 60)
    @Operation(summary = "为用户分配角色", description = "为用户分配角色，需要管理员权限")
    public Result<Void> assignRolesToUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Valid @RequestBody AssignUserRolesRequest request) {
        log.info("为用户分配角色 - 用户ID: {}, 角色数量: {}", userId, request.getRoleIds().size());
        userRoleService.assignRolesToUser(userId, request.getRoleIds());
        return Result.success();
    }

    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('system:user:query')")
    @Operation(summary = "查询用户角色", description = "查询用户已分配的角色列表")
    public Result<List<Long>> getUserRoles(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        log.info("查询用户角色 - 用户ID: {}", userId);
        List<Long> roleIds = userRoleService.getUserRoles(userId);
        return Result.success(roleIds);
    }

    @DeleteMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('system:user:assign')")
    @RateLimit(limit = 20, timeout = 60)
    @Operation(summary = "移除用户角色", description = "移除用户的指定角色，需要管理员权限")
    public Result<Void> removeRolesFromUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody AssignUserRolesRequest request) {
        log.info("移除用户角色 - 用户ID: {}, 移除角色数量: {}", userId, request.getRoleIds().size());
        userRoleService.removeRolesFromUser(userId, request.getRoleIds());
        return Result.success();
    }

    @DeleteMapping("/{userId}/roles/all")
    @PreAuthorize("hasAuthority('system:user:assign')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "清空用户角色", description = "清空用户的所有角色，需要管理员权限")
    public Result<Void> clearUserRoles(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        log.info("清空用户角色 - 用户ID: {}", userId);
        userRoleService.clearUserRoles(userId);
        return Result.success();
    }
}
