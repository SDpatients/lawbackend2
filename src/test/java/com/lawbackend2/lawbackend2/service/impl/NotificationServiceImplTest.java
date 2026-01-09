package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.Notification;
import com.lawbackend2.lawbackend2.repository.NotificationRepository;
import com.lawbackend2.lawbackend2.service.NotificationService;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private WebSocketService webSocketService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_ACCOUNT = "testuser";
    private static final String TEST_USER_NAME = "测试用户";
    private static final Long TEST_NOTIFICATION_ID = 1L;

    @BeforeEach
    void setUp() {
        Notification mockNotification = Notification.builder()
                .id(TEST_NOTIFICATION_ID)
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .title("测试通知")
                .content("这是一条测试通知")
                .type("SYSTEM")
                .isRead(false)
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
        when(notificationRepository.findById(eq(TEST_NOTIFICATION_ID)))
                .thenReturn(java.util.Optional.of(mockNotification));
        when(notificationRepository.findByUserId(eq(TEST_USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockNotification), PageRequest.of(0, 10), 1));
        when(notificationRepository.findByUserIdAndIsRead(eq(TEST_USER_ID), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockNotification), PageRequest.of(0, 10), 1));
        when(notificationRepository.findByUserIdAndStatus(eq(TEST_USER_ID), eq("ACTIVE"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockNotification), PageRequest.of(0, 10), 1));
        when(notificationRepository.findByUserIdAndIsReadOrderByCreateTimeDesc(eq(TEST_USER_ID), eq(false)))
                .thenReturn(List.of(mockNotification));
        when(notificationRepository.countUnreadByUserId(eq(TEST_USER_ID))).thenReturn(1L);
        when(notificationRepository.countByUserIdAndStatus(eq(TEST_USER_ID), eq("ACTIVE"))).thenReturn(1L);
    }

    @Test
    void testCreateNotification_Success() {
        Notification notification = Notification.builder()
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .title("测试通知")
                .content("这是一条测试通知")
                .type("SYSTEM")
                .build();

        Notification createdNotification = notificationService.createNotification(notification);

        assertNotNull(createdNotification);
        assertEquals(TEST_USER_ID, createdNotification.getUserId());
        assertEquals("测试通知", createdNotification.getTitle());
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(webSocketService, times(1)).sendNotificationToUser(eq(TEST_USER_ID), any());
    }

    @Test
    void testCreateNotification_WithAllParameters() {
        Notification mockNotification = Notification.builder()
                .id(TEST_NOTIFICATION_ID)
                .userId(TEST_USER_ID)
                .userAccount(TEST_USER_ACCOUNT)
                .userName(TEST_USER_NAME)
                .title("测试标题")
                .content("测试内容")
                .type("SYSTEM")
                .priority("HIGH")
                .isRead(false)
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .build();
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);
        
        Notification notification = notificationService.createNotification(
                TEST_USER_ID, TEST_USER_ACCOUNT, TEST_USER_NAME,
                "测试标题", "测试内容", "SYSTEM",
                100L, "CASE", "HIGH", 2L, "管理员"
        );

        assertNotNull(notification);
        assertEquals(TEST_USER_ID, notification.getUserId());
        assertEquals("测试标题", notification.getTitle());
        assertEquals("HIGH", notification.getPriority());
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(webSocketService, times(1)).sendNotificationToUser(eq(TEST_USER_ID), any());
    }

    @Test
    void testGetNotificationById_Success() {
        Notification notification = notificationService.getNotificationById(TEST_NOTIFICATION_ID);

        assertNotNull(notification);
        assertEquals(TEST_NOTIFICATION_ID, notification.getId());
        verify(notificationRepository, times(1)).findById(TEST_NOTIFICATION_ID);
    }

    @Test
    void testGetNotificationById_NotFound() {
        when(notificationRepository.findById(eq(999L)))
                .thenReturn(java.util.Optional.empty());

        assertThrows(Exception.class, () -> notificationService.getNotificationById(999L));
    }

    @Test
    void testGetUserNotifications_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = notificationService.getUserNotifications(TEST_USER_ID, pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(notificationRepository, times(1)).findByUserId(TEST_USER_ID, pageable);
    }

    @Test
    void testGetUserNotificationsByStatus_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = notificationService.getUserNotificationsByStatus(TEST_USER_ID, "ACTIVE", pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(notificationRepository, times(1)).findByUserIdAndStatus(TEST_USER_ID, "ACTIVE", pageable);
    }

    @Test
    void testGetUserNotificationsByReadStatus_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = notificationService.getUserNotificationsByReadStatus(TEST_USER_ID, false, pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(notificationRepository, times(1)).findByUserIdAndIsRead(TEST_USER_ID, false, pageable);
    }

    @Test
    void testGetUnreadNotifications_Success() {
        List<Notification> notifications = notificationService.getUnreadNotifications(TEST_USER_ID);

        assertNotNull(notifications);
        verify(notificationRepository, times(1)).findByUserIdAndIsReadOrderByCreateTimeDesc(TEST_USER_ID, false);
    }

    @Test
    void testCountUnreadNotifications_Success() {
        Long count = notificationService.countUnreadNotifications(TEST_USER_ID);

        assertEquals(1L, count);
        verify(notificationRepository, times(1)).countUnreadByUserId(TEST_USER_ID);
    }

    @Test
    void testCountNotificationsByStatus_Success() {
        Long count = notificationService.countNotificationsByStatus(TEST_USER_ID, "ACTIVE");

        assertEquals(1L, count);
        verify(notificationRepository, times(1)).countByUserIdAndStatus(TEST_USER_ID, "ACTIVE");
    }

    @Test
    void testMarkAsRead_Success() {
        Notification notification = notificationService.markAsRead(TEST_NOTIFICATION_ID);

        assertNotNull(notification);
        assertTrue(notification.getIsRead());
        assertNotNull(notification.getReadTime());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testMarkAllAsRead_Success() {
        notificationService.markAllAsRead(TEST_USER_ID);

        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testDeleteNotification_Success() {
        notificationService.deleteNotification(TEST_NOTIFICATION_ID);

        verify(notificationRepository, times(1)).delete(any(Notification.class));
    }

    @Test
    void testBatchDeleteNotifications_Success() {
        notificationService.batchDeleteNotifications(List.of(TEST_NOTIFICATION_ID));

        verify(notificationRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void testUpdateNotificationStatus_Success() {
        notificationService.updateNotificationStatus(TEST_NOTIFICATION_ID, "INACTIVE");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testDeleteExpiredNotifications_Success() {
        notificationService.deleteExpiredNotifications();

        verify(notificationRepository, times(1)).deleteExpiredNotifications(any(LocalDateTime.class));
    }
}
