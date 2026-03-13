package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.Notification;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.entity.UserRole;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.NotificationRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.service.impl.NotificationServiceImpl;
import com.lawbackend2.lawbackend2.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private WebSocketService webSocketService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @InjectMocks
    private NotificationServiceImpl notificationService;
    private User mockAdminUser;
    private User mockSuperAdminUser;
    private Role adminRole;
    private Role superAdminRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setRoleCode("ADMIN");
        adminRole.setRoleName("管理员");
        adminRole.setIsDeleted(false);
        superAdminRole = new Role();
        superAdminRole.setId(2L);
        superAdminRole.setRoleCode("SUPER_ADMIN");
        superAdminRole.setRoleName("超级管理员");
        superAdminRole.setIsDeleted(false);
        mockAdminUser = new User();
        mockAdminUser.setId(1L);
        mockAdminUser.setUsername("admin");
        mockAdminUser.setRealName("管理员");
        mockAdminUser.setStatus("ACTIVE");
        mockAdminUser.setIsDeleted(false);
        mockSuperAdminUser = new User();
        mockSuperAdminUser.setId(2L);
        mockSuperAdminUser.setUsername("superadmin");
        mockSuperAdminUser.setRealName("超级管理员");
        mockSuperAdminUser.setStatus("ACTIVE");
        mockSuperAdminUser.setIsDeleted(false);
    }

    @Test
    void testSendNotificationToAdminAndSuperAdmin_Success() {
        when(userRoleRepository.findUserIdsByRoleCodes(Arrays.asList("ADMIN", "SUPER_ADMIN")))
                .thenReturn(Arrays.asList(1L, 2L));
        when(userRepository.findAllById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(mockAdminUser, mockSuperAdminUser));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(1L);
            return notification;
        });
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "测试标题",
                "测试内容",
                "TEST_TYPE",
                100L,
                "TestEntity",
                1L,
                "测试用户"
        );
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(webSocketService, times(2)).sendNotificationToUser(any(Long.class), any());
    }
    @Test
    void testSendNotificationToAdminAndSuperAdmin_NoUsers() {
        when(userRoleRepository.findUserIdsByRoleCodes(Arrays.asList("ADMIN", "SUPER_ADMIN")))
                .thenReturn(Arrays.asList());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "测试标题",
                "测试内容",
                "TEST_TYPE",
                100L,
                "TestEntity",
                1L,
                "测试用户"
        );
        verify(notificationRepository, never()).save(any(Notification.class));
        verify(webSocketService, never()).sendNotificationToUser(any(Long.class), any());
    }
    @Test
    void testCreateNotification_Success() {
        Notification notification = Notification.builder()
                .userId(1L)
                .userAccount("test")
                .userName("测试用户")
                .title("测试标题")
                .content("测试内容")
                .type("TEST")
                .isRead(false)
                .build();
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        Notification result = notificationService.createNotification(notification);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(webSocketService, times(1)).sendNotificationToUser(eq(1L), any());
    }
    @Test
    void testGetNotificationById_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setTitle("测试标题");
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        Notification result = notificationService.getNotificationById(1L, 1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试标题", result.getTitle());
        verify(notificationRepository, times(1)).findById(1L);
    }
    @Test
    void testGetNotificationById_NotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            notificationService.getNotificationById(999L, 1L);
        });
        assertEquals("通知不存在", exception.getMessage());
    }
    @Test
    void testMarkAsRead_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setIsRead(false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        Notification result = notificationService.markAsRead(1L, 1L);
        assertTrue(result.getIsRead());
        assertNotNull(result.getReadTime());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
    @Test
    void testDeleteNotification_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        doNothing().when(notificationRepository).delete(notification);
        notificationService.deleteNotification(1L, 1L);
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).delete(notification);
    }
    @Test
    void testUpdateNotificationStatus_Success() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setStatus("ACTIVE");
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        notificationService.updateNotificationStatus(1L, "INACTIVE", 1L);
        assertEquals("INACTIVE", notification.getStatus());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
