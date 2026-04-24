package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.ChangePasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.ForgotPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.RefreshTokenRequest;
import com.lawbackend2.lawbackend2.dto.request.SendSmsCodeRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdateUserEmailRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdateUserMobileRequest;
import com.lawbackend2.lawbackend2.dto.request.UpdateUserRealNameRequest;
import com.lawbackend2.lawbackend2.dto.request.UserLoginRequest;
import com.lawbackend2.lawbackend2.dto.response.RefreshTokenResponse;
import com.lawbackend2.lawbackend2.dto.response.UserInfoResponse;
import com.lawbackend2.lawbackend2.dto.response.UserLoginResponse;
import com.lawbackend2.lawbackend2.dto.response.UserResponse;
import com.lawbackend2.lawbackend2.dto.response.UserRoleCheckResponse;
import com.lawbackend2.lawbackend2.service.SmsService;
import com.lawbackend2.lawbackend2.service.UserService;
import com.lawbackend2.lawbackend2.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "用户登录、注册、Token管理、密码重置")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserRoleService userRoleService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名密码登录，获取JWT Token")
    @AuditLog(module = "auth", moduleName = "用户认证", operationType = "LOGIN", operationName = "用户登录", recordParams = false)
    public Result<UserLoginResponse> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String deviceInfo = httpRequest.getHeader("User-Agent");
        
        UserLoginResponse response = userService.login(
                request.getUsername(),
                request.getPassword(),
                request.getSmsCode(),
                ipAddress,
                deviceInfo
        );
        
        return Result.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，使Token失效")
    @AuditLog(module = "auth", moduleName = "用户认证", operationType = "LOGOUT", operationName = "用户登出")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userService.logout(token);
        }
        return Result.success();
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新访问令牌", description = "使用刷新令牌获取新的访问令牌")
    public Result<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = userService.refreshAccessToken(request.getRefreshToken());
        return Result.success(response);
    }

    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户信息", description = "根据JWT Token获取当前登录用户的详细信息，包括角色和权限")
    public Result<UserInfoResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Long userId = (Long) principal;

        log.info("获取当前用户信息 - 用户ID: {}", userId);
        UserInfoResponse response = userService.getCurrentUserInfo(userId);
        return Result.success(response);
    }

    @GetMapping("/check-admin")
    @Operation(summary = "检查当前用户是否为管理员", description = "检查当前登录用户是否具有ADMIN或SUPER_ADMIN角色")
    public Result<UserRoleCheckResponse> checkAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Long userId = (Long) principal;

        List<String> roleCodes = userRoleService.getUserRoleCodes(userId);
        boolean isAdmin = roleCodes.contains("ADMIN") || roleCodes.contains("SUPER_ADMIN");
        boolean isSuperAdmin = roleCodes.contains("SUPER_ADMIN");

        UserRoleCheckResponse response = UserRoleCheckResponse.builder()
                .userId(userId)
                .username(authentication.getName())
                .isAdmin(isAdmin)
                .isSuperAdmin(isSuperAdmin)
                .roles(roleCodes)
                .build();

        log.info("检查管理员权限 - 用户ID: {}, isAdmin: {}, isSuperAdmin: {}", userId, isAdmin, isSuperAdmin);
        return Result.success(response);
    }

    @GetMapping("/check-role/{roleCode}")
    @Operation(summary = "检查当前用户是否具有指定角色", description = "检查当前登录用户是否具有指定的角色")
    public Result<UserRoleCheckResponse> checkRole(
            @PathVariable String roleCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Long userId = (Long) principal;

        List<String> roleCodes = userRoleService.getUserRoleCodes(userId);
        boolean hasRole = roleCodes.contains(roleCode);
        boolean isAdmin = roleCodes.contains("ADMIN") || roleCodes.contains("SUPER_ADMIN");
        boolean isSuperAdmin = roleCodes.contains("SUPER_ADMIN");

        UserRoleCheckResponse response = UserRoleCheckResponse.builder()
                .userId(userId)
                .username(authentication.getName())
                .isAdmin(isAdmin)
                .isSuperAdmin(isSuperAdmin)
                .roles(roleCodes)
                .build();

        log.info("检查角色权限 - 用户ID: {}, roleCode: {}, hasRole: {}", userId, roleCode, hasRole);
        return Result.success(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改当前用户密码", description = "当前登录用户修改自己的密码，需要提供原密码和新密码")
    @AuditLog(module = "auth", moduleName = "用户认证", operationType = "UPDATE", operationName = "修改密码", recordParams = false)
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return Result.error(401, "用户未登录或Token已过期");
        }

        Long userId = (Long) principal;

        log.info("修改密码请求 - 用户ID: {}", userId);
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @PostMapping("/forgot-password/send-sms")
    @Operation(summary = "发送忘记密码短信验证码", description = "向指定手机号发送用于重置密码的短信验证码，短信类型为3")
    public Result<Void> sendForgotPasswordSms(@Valid @RequestBody SendSmsCodeRequest request) {
        log.info("发送忘记密码短信验证码 - 手机号: {}", request.getMobile());
        smsService.sendSmsCode(request.getMobile(), "3");
        return Result.success();
    }

    @PostMapping("/forgot-password/reset")
    @Operation(summary = "忘记密码重置", description = "通过手机号和短信验证码重置密码，无需登录")
    public Result<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("忘记密码重置请求 - 手机号：{}", request.getMobile());
        userService.forgotPassword(request.getMobile(), request.getSmsCode(), request.getNewPassword());
        return Result.success();
    }

    @PutMapping("/profile/mobile")
    @Operation(summary = "修改当前用户手机号", description = "当前登录用户修改自己的手机号")
    public Result<UserResponse> updateCurrentMobile(
            @Valid @RequestBody UpdateUserMobileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未登录或 Token 已过期");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return Result.error(401, "用户未登录或 Token 已过期");
        }

        Long userId = (Long) principal;

        log.info("修改当前用户手机号 - 用户 ID: {}, 新手机号：{}", userId, request.getMobile());
        UserResponse response = userService.updateUserMobile(userId, request.getMobile(), request.getSmsCode());
        return Result.success(response);
    }

    @PutMapping("/profile/email")
    @Operation(summary = "修改当前用户邮箱", description = "当前登录用户修改自己的邮箱")
    public Result<UserResponse> updateCurrentEmail(
            @Valid @RequestBody UpdateUserEmailRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未登录或 Token 已过期");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return Result.error(401, "用户未登录或 Token 已过期");
        }

        Long userId = (Long) principal;

        log.info("修改当前用户邮箱 - 用户 ID: {}, 新邮箱：{}", userId, request.getEmail());
        UserResponse response = userService.updateUserEmail(userId, request.getEmail());
        return Result.success(response);
    }

    @PutMapping("/profile/real-name")
    @Operation(summary = "修改当前用户真实姓名", description = "当前登录用户修改自己的真实姓名")
    public Result<UserResponse> updateCurrentRealName(
            @Valid @RequestBody UpdateUserRealNameRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "用户未登录或 Token 已过期");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long)) {
            return Result.error(401, "用户未登录或 Token 已过期");
        }

        Long userId = (Long) principal;

        log.info("修改当前用户真实姓名 - 用户 ID: {}, 新姓名：{}", userId, request.getRealName());
        UserResponse response = userService.updateUserRealName(userId, request.getRealName());
        return Result.success(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
