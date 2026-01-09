package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.dto.response.PageResponse;
import com.lawbackend2.lawbackend2.entity.LoginRecord;
import com.lawbackend2.lawbackend2.service.LoginRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "登录日志管理", description = "登录日志管理相关接口")
public class LoginRecordController {

    @Autowired
    private LoginRecordService loginRecordService;

    @GetMapping("/login-history")
    @Operation(summary = "登录历史查询", description = "分页查询登录历史记录")
    public ApiResponse<PageResponse<LoginRecord>> getLoginHistory(
            Authentication authentication,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "登录状态") @RequestParam(required = false) String loginStatus) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            userId = (Long) authentication.getPrincipal();
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "loginTime");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<LoginRecord> page;
        if (userId != null) {
            page = loginRecordService.getUserLoginHistory(userId, pageable);
        } else if (loginStatus != null) {
            page = loginRecordService.getLoginHistoryByStatus(loginStatus, pageable);
        } else {
            return ApiResponse.error(400, "必须提供用户ID或登录状态");
        }

        PageResponse<LoginRecord> response = PageResponse.of(
                page.getTotalElements(),
                page.getContent()
        );

        return ApiResponse.success(response);
    }

    @GetMapping("/recent-failed")
    @Operation(summary = "最近失败登录", description = "获取最近7天的失败登录记录")
    public ApiResponse<List<LoginRecord>> getRecentFailedLogins(
            @Parameter(description = "天数") @RequestParam(defaultValue = "7") Integer days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        List<LoginRecord> failedLogins = loginRecordService.getRecentFailedLogins(startTime);
        return ApiResponse.success(failedLogins);
    }

    @GetMapping("/statistics")
    @Operation(summary = "登录统计", description = "统计用户成功登录次数")
    public ApiResponse<Long> getLoginStatistics(
            Authentication authentication,
            @Parameter(description = "天数") @RequestParam(defaultValue = "30") Integer days) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            userId = (Long) authentication.getPrincipal();
        }

        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        long count = loginRecordService.countSuccessfulLoginsSince(userId, startTime);

        return ApiResponse.success(count);
    }
}
