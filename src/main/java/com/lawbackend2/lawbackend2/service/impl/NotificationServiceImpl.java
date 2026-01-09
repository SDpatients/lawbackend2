package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.WebSocketMessage;
import com.lawbackend2.lawbackend2.entity.Notification;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.NotificationRepository;
import com.lawbackend2.lawbackend2.service.NotificationService;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketService webSocketService;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                              WebSocketService webSocketService) {
        this.notificationRepository = notificationRepository;
        this.webSocketService = webSocketService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createNotification(Notification notification) {
        log.info("创建通知, 用户ID: {}, 标题: {}", notification.getUserId(), notification.getTitle());
        Notification savedNotification = notificationRepository.save(notification);
        
        WebSocketMessage message = WebSocketMessage.builder()
                .type("NEW_NOTIFICATION")
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .data(savedNotification)
                .timestamp(System.currentTimeMillis())
                .build();
        webSocketService.sendNotificationToUser(notification.getUserId(), message);
        
        return savedNotification;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createNotification(Long userId, String userAccount, String userName,
                                           String title, String content, String type,
                                           Long relatedId, String relatedType,
                                           String priority, Long createUserId, String createUserName) {
        Notification notification = Notification.builder()
                .userId(userId)
                .userAccount(userAccount)
                .userName(userName)
                .title(title)
                .content(content)
                .type(type)
                .isRead(false)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .priority(priority != null ? priority : "NORMAL")
                .status("ACTIVE")
                .createUserId(createUserId)
                .createUserName(createUserName)
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("通知不存在"));
    }

    @Override
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Notification> getUserNotificationsByStatus(Long userId, String status, Pageable pageable) {
        return notificationRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Override
    public Page<Notification> getUserNotificationsByType(Long userId, String type, Pageable pageable) {
        return notificationRepository.findByUserIdAndType(userId, type, pageable);
    }

    @Override
    public Page<Notification> getUserNotificationsByReadStatus(Long userId, Boolean isRead, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsRead(userId, isRead, pageable);
    }

    @Override
    public Page<Notification> searchNotifications(Long userId, String type, Boolean isRead, String status, Pageable pageable) {
        return notificationRepository.searchNotifications(userId, type, isRead, status, pageable);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreateTimeDesc(userId, false);
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public Long countNotificationsByStatus(Long userId, String status) {
        return notificationRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification markAsRead(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.setIsRead(true);
        notification.setReadTime(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadTime(now);
        }
        notificationRepository.saveAll(unreadNotifications);
        log.info("标记用户 {} 的所有通知为已读, 共 {} 条", userId, unreadNotifications.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notificationRepository.delete(notification);
        log.info("删除通知, ID: {}", notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpiredNotifications() {
        LocalDateTime now = LocalDateTime.now();
        notificationRepository.deleteExpiredNotifications(now);
        log.info("清理过期通知");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteNotifications(List<Long> notificationIds) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        notificationRepository.deleteAll(notifications);
        log.info("批量删除通知, 共 {} 条", notifications.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotificationStatus(Long notificationId, String status) {
        Notification notification = getNotificationById(notificationId);
        notification.setStatus(status);
        notificationRepository.save(notification);
        log.info("更新通知状态, ID: {}, 状态: {}", notificationId, status);
    }
}
