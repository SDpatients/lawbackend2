package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.entity.Notification;
import com.lawbackend2.lawbackend2.service.NotificationService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notification")
@Tag(name = "通知管理", description = "通知管理相关接口")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "创建通知", description = "创建新的系统通知")
    public ResponseEntity<ApiResponse<Notification>> createNotification(@RequestBody Notification notification) {
        log.info("创建通知请求, 用户ID: {}, 标题: {}", notification.getUserId(), notification.getTitle());
        Notification createdNotification = notificationService.createNotification(notification);
        return ResponseEntity.ok(ApiResponse.success(createdNotification));
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "获取通知详情", description = "根据通知ID获取通知详情")
    public ResponseEntity<ApiResponse<Notification>> getNotificationById(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        Notification notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification));
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户通知列表", description = "分页获取用户的通知列表")
    public ResponseEntity<ApiResponse<Page<Notification>>> getUserNotifications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索通知", description = "根据条件搜索用户通知")
    public ResponseEntity<ApiResponse<Page<Notification>>> searchNotifications(
            @Parameter(description = "通知类型") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "是否已读") @RequestParam(value = "isRead", required = false) Boolean isRead,
            @Parameter(description = "通知状态") @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Notification> notifications = notificationService.searchNotifications(userId, type, isRead, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread")
    @Operation(summary = "获取未读通知列表", description = "获取用户的所有未读通知")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/count/unread")
    @Operation(summary = "获取未读通知数量", description = "统计用户的未读通知数量")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications() {
        Long userId = SecurityUtil.getCurrentUserId();
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "标记通知为已读", description = "将指定通知标记为已读状态")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        Notification notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification));
    }

    @PutMapping("/read-all")
    @Operation(summary = "标记所有通知为已读", description = "将用户的所有通知标记为已读状态")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "删除通知", description = "删除指定的通知")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除通知", description = "批量删除多个通知")
    public ResponseEntity<ApiResponse<Void>> batchDeleteNotifications(
            @Parameter(description = "通知ID列表") @RequestBody List<Long> notificationIds) {
        notificationService.batchDeleteNotifications(notificationIds);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{notificationId}/status")
    @Operation(summary = "更新通知状态", description = "更新通知的状态")
    public ResponseEntity<ApiResponse<Void>> updateNotificationStatus(
            @Parameter(description = "通知ID") @PathVariable("notificationId") Long notificationId,
            @Parameter(description = "通知状态") @RequestParam("status") String status) {
        notificationService.updateNotificationStatus(notificationId, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
