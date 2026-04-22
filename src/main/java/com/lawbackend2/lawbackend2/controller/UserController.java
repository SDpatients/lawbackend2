package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.annotation.RateLimit;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.*;
import com.lawbackend2.lawbackend2.dto.response.UserListResponse;
import com.lawbackend2.lawbackend2.dto.response.UserResponse;
import com.lawbackend2.lawbackend2.service.UserService;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户CRUD操作API")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "创建用户", description = "创建新用户，需要管理员权限")
    @AuditLog(module = "user", moduleName = "用户管理", operationType = "CREATE", operationName = "创建用户")
    public Result<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("创建用户请求 - 用户名: {}", request.getUsername());
        UserResponse response = userService.createUser(request);
        return Result.success(response);
    }

    @GetMapping
    @RateLimit(limit = 50, timeout = 60)
    @Operation(summary = "获取用户列表", description = "分页查询用户列表，支持排序和筛选")
    public Result<UserListResponse> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortField,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortOrder,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "用户状态") @RequestParam(required = false) String status) {
        
        UserQueryRequest request = UserQueryRequest.builder()
                .page(page)
                .size(size)
                .sortField(sortField)
                .sortOrder(sortOrder)
                .keyword(keyword)
                .status(status)
                .build();
        
        UserListResponse response = userService.getUserList(request);
        return Result.success(response);
    }

    @GetMapping("/admins")
    @RateLimit(limit = 50, timeout = 60)
    @Operation(summary = "获取所有管理员用户", description = "获取所有role_code为ADMIN的用户基本信息")
    public Result<List<UserResponse>> getAdminUsers() {
        log.info("获取所有管理员用户请求");
        List<UserResponse> adminUsers = userService.getAdminUsers();
        return Result.success(adminUsers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    @Operation(summary = "获取单个用户", description = "根据用户ID查询用户详情")
    public Result<UserResponse> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("查询用户详情 - 用户ID: {}", id);
        UserResponse response = userService.getUserById(id);
        return Result.success(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Operation(summary = "更新用户", description = "全量更新用户信息，需要管理员权限")
    @AuditLog(module = "user", moduleName = "用户管理", operationType = "UPDATE", operationName = "更新用户")
    public Result<UserResponse> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户信息 - 用户ID: {}", id);
        UserResponse response = userService.updateUser(id, request);
        return Result.success(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @RateLimit(limit = 20, timeout = 60)
    @Operation(summary = "部分更新用户", description = "部分更新用户信息，需要管理员权限")
    public Result<UserResponse> patchUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody UserPatchRequest request) {
        log.info("部分更新用户信息 - 用户ID: {}", id);
        UserResponse response = userService.patchUser(id, request);
        return Result.success(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "删除用户", description = "逻辑删除用户，需要管理员权限")
    @AuditLog(module = "user", moduleName = "用户管理", operationType = "DELETE", operationName = "删除用户")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("删除用户 - 用户ID: {}", id);
        userService.deleteUserById(id);
        return Result.success();
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAuthority('system:user:query')")
    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询用户详情")
    public Result<UserResponse> getUserByUsername(
            @Parameter(description = "用户名") @PathVariable String username) {
        log.info("根据用户名查询用户 - 用户名: {}", username);
        UserResponse response = userService.getUserByUsername(username);
        return Result.success(response);
    }

    @GetMapping("/mobile/{mobile}")
    @PreAuthorize("hasAuthority('system:user:query')")
    @Operation(summary = "根据手机号查询用户", description = "根据手机号查询用户详情")
    public Result<UserResponse> getUserByMobile(
            @Parameter(description = "手机号") @PathVariable String mobile) {
        log.info("根据手机号查询用户 - 手机号: {}", mobile);
        UserResponse response = userService.getUserByMobile(mobile);
        return Result.success(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @RateLimit(limit = 20, timeout = 60)
    @Operation(summary = "更新用户状态", description = "更新指定用户的状态，需要管理员权限")
    @AuditLog(module = "user", moduleName = "用户管理", operationType = "UPDATE", operationName = "更新用户状态")
    public Result<UserResponse> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        log.info("更新用户状态 - 用户ID: {}, 新状态: {}", id, request.getStatus());
        UserResponse response = userService.updateUserStatus(id, request.getStatus());
        return Result.success(response);
    }

    @PutMapping("/{id}/mobile")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "更新用户手机号", description = "更新指定用户的手机号，需要管理员权限")
    public Result<UserResponse> updateUserMobile(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserMobileRequest request) {
        log.info("更新用户手机号 - 用户ID: {}, 新手机号: {}", id, request.getMobile());
        UserResponse response = userService.updateUserMobile(id, request.getMobile(), request.getSmsCode());
        return Result.success(response);
    }

    @PutMapping("/{id}/email")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "更新用户邮箱", description = "更新指定用户的邮箱，需要管理员权限")
    public Result<UserResponse> updateUserEmail(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserEmailRequest request) {
        log.info("更新用户邮箱 - 用户ID: {}, 新邮箱: {}", id, request.getEmail());
        UserResponse response = userService.updateUserEmail(id, request.getEmail());
        return Result.success(response);
    }

    @PutMapping("/batch/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    @RateLimit(limit = 5, timeout = 60)
    @Operation(summary = "批量更新用户状态", description = "批量更新多个用户的状态，需要管理员权限")
    @AuditLog(module = "user", moduleName = "用户管理", operationType = "BATCH_UPDATE", operationName = "批量更新用户状态")
    public Result<Void> batchUpdateUserStatus(
            @Valid @RequestBody BatchUpdateUserStatusRequest request) {
        log.info("批量更新用户状态 - 用户数量: {}, 新状态: {}", request.getUserIds().size(), request.getStatus());
        userService.batchUpdateUserStatus(request.getUserIds(), request.getStatus());
        return Result.success();
    }
}
