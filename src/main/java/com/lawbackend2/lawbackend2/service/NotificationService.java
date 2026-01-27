package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    Notification createNotification(Notification notification);

    Notification createNotification(Long userId, String userAccount, String userName,
                               String title, String content, String type,
                               Long relatedId, String relatedType,
                               String priority, Long createUserId, String createUserName);

    Notification getNotificationById(Long notificationId);

    Page<Notification> getUserNotifications(Long userId, Pageable pageable);

    Page<Notification> getUserNotificationsByStatus(Long userId, String status, Pageable pageable);

    Page<Notification> getUserNotificationsByType(Long userId, String type, Pageable pageable);

    Page<Notification> getUserNotificationsByReadStatus(Long userId, Boolean isRead, Pageable pageable);

    Page<Notification> searchNotifications(Long userId, String type, Boolean isRead, String status, Pageable pageable);

    List<Notification> getUnreadNotifications(Long userId);

    Long countUnreadNotifications(Long userId);

    Long countNotificationsByStatus(Long userId, String status);

    Notification markAsRead(Long notificationId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long notificationId);

    void deleteExpiredNotifications();

    void batchDeleteNotifications(List<Long> notificationIds);

    void updateNotificationStatus(Long notificationId, String status);

    void sendNotificationToAdminAndSuperAdmin(String title, String content, String type, Long relatedId, String relatedType, Long createUserId, String createUserName);

    void sendNotificationToUser(Long userId, String title, String content, String type, Long relatedId, String relatedType, Long createUserId, String createUserName);
}
