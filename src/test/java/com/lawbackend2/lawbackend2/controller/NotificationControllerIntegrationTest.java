package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.entity.Notification;
import com.lawbackend2.lawbackend2.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockNotification = Notification.builder()
                .id(1L)
                .userId(1L)
                .userAccount("testuser")
                .userName("测试用户")
                .title("测试通知")
                .content("这是一条测试通知")
                .type("SYSTEM")
                .isRead(false)
                .status("ACTIVE")
                .createTime(LocalDateTime.now())
                .build();

        when(notificationService.createNotification(any(Notification.class))).thenReturn(mockNotification);
        when(notificationService.getNotificationById(1L)).thenReturn(mockNotification);
        when(notificationService.markAsRead(1L)).thenReturn(mockNotification);
    }

    @Test
    void testCreateNotification_Success() throws Exception {
        mockMvc.perform(post("/api/v1/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockNotification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("测试通知"));
    }

    @Test
    void testGetNotificationById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/notification/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        mockMvc.perform(put("/api/v1/notification/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
