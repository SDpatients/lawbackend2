package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.RateLimit;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.*;
import com.lawbackend2.lawbackend2.dto.response.UserListResponse;
import com.lawbackend2.lawbackend2.dto.response.UserResponse;
import com.lawbackend2.lawbackend2.service.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户CRUD操作API")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "创建用户", description = "创建新用户，需要管理员权限")
    public Result<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("创建用户请求 - 用户名: {}", request.getUsername());
        UserResponse response = userService.createUser(request);
        return Result.success(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    @Operation(summary = "获取单个用户", description = "根据用户ID查询用户详情")
    public Result<UserResponse> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("查询用户详情 - 用户ID: {}", id);
        UserResponse response = userService.getUserById(id);
        return Result.success(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "更新用户", description = "全量更新用户信息，需要管理员权限")
    public Result<UserResponse> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户信息 - 用户ID: {}", id);
        UserResponse response = userService.updateUser(id, request);
        return Result.success(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
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
    @PreAuthorize("hasAuthority('user:delete')")
    @RateLimit(limit = 10, timeout = 60)
    @Operation(summary = "删除用户", description = "逻辑删除用户，需要管理员权限")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        log.info("删除用户 - 用户ID: {}", id);
        userService.deleteUserById(id);
        return Result.success();
    }
}
