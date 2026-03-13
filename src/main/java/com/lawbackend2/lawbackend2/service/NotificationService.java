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

    Notification getNotificationById(Long notificationId, Long currentUserId);

    Page<Notification> getUserNotifications(Long userId, Long currentUserId, Pageable pageable);

    Page<Notification> getUserNotificationsByStatus(Long userId, Long currentUserId, String status, Pageable pageable);

    Page<Notification> getUserNotificationsByType(Long userId, Long currentUserId, String type, Pageable pageable);

    Page<Notification> getUserNotificationsByReadStatus(Long userId, Long currentUserId, Boolean isRead, Pageable pageable);

    Page<Notification> searchNotifications(Long userId, Long currentUserId, String type, Boolean isRead, String status, Pageable pageable);

    List<Notification> getUnreadNotifications(Long userId, Long currentUserId);

    Long countUnreadNotifications(Long userId, Long currentUserId);

    Long countNotificationsByStatus(Long userId, Long currentUserId, String status);

    Notification markAsRead(Long notificationId, Long currentUserId);

    void markAllAsRead(Long userId, Long currentUserId);

    void deleteNotification(Long notificationId, Long currentUserId);

    void deleteExpiredNotifications();

    void batchDeleteNotifications(List<Long> notificationIds, Long currentUserId);

    void updateNotificationStatus(Long notificationId, String status, Long currentUserId);

    void sendNotificationToAdminAndSuperAdmin(String title, String content, String type, Long relatedId, String relatedType, Long createUserId, String createUserName);

    void sendNotificationToUser(Long userId, String title, String content, String type, Long relatedId, String relatedType, Long createUserId, String createUserName);
}
