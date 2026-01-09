package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.dto.response.PageResponse;
import com.lawbackend2.lawbackend2.entity.Activity;
import com.lawbackend2.lawbackend2.service.ActivityService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/activity")
@Tag(name = "操作日志管理", description = "操作日志管理相关接口")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/list")
    @Operation(summary = "操作日志列表", description = "分页查询操作日志")
    public ApiResponse<PageResponse<Activity>> listActivities(
            Authentication authentication,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "操作类型") @RequestParam(required = false) String type,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            userId = (Long) authentication.getPrincipal();
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<Activity> page = activityService.searchActivities(userId, type, status, pageable);

        PageResponse<Activity> response = PageResponse.of(
                page.getTotalElements(),
                page.getContent()
        );

        return ApiResponse.success(response);
    }

    @GetMapping("/recent")
    @Operation(summary = "最近操作日志", description = "获取最近7天的操作日志")
    public ApiResponse<List<Activity>> getRecentActivities(
            @Parameter(description = "操作类型") @RequestParam(required = false) String type) {
        List<Activity> activities;
        if (type != null && !type.isEmpty()) {
            activities = activityService.getRecentActivitiesByType(type);
        } else {
            activities = activityService.getRecentActivitiesByType("LOGIN");
        }
        return ApiResponse.success(activities);
    }

    @GetMapping("/related")
    @Operation(summary = "关联操作日志", description = "查询关联实体的操作日志")
    public ApiResponse<List<Activity>> getRelatedActivities(
            @Parameter(description = "关联类型") @RequestParam String relatedType,
            @Parameter(description = "关联ID") @RequestParam Long relatedId) {
        List<Activity> activities = activityService.getActivitiesByRelatedTypeAndRelatedId(relatedType, relatedId);
        return ApiResponse.success(activities);
    }

    @GetMapping("/count")
    @Operation(summary = "统计操作日志", description = "统计用户在指定时间后的操作次数")
    public ApiResponse<Long> countActivities(
            Authentication authentication,
            @Parameter(description = "开始时间（天数）") @RequestParam(defaultValue = "7") Integer days) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            userId = (Long) authentication.getPrincipal();
        }

        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }

        java.time.LocalDateTime startTime = java.time.LocalDateTime.now().minusDays(days);
        long count = activityService.countActivitiesByUserSince(userId, startTime);

        return ApiResponse.success(count);
    }
}
