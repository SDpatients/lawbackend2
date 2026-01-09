package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.dto.response.PageResponse;
import com.lawbackend2.lawbackend2.entity.Activity;
import com.lawbackend2.lawbackend2.service.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ActivityService activityService;

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ACTIVITY_ID = 1L;

    @BeforeEach
    void setUp() {
        Activity mockActivity = Activity.builder()
                .id(TEST_ACTIVITY_ID)
                .userId(TEST_USER_ID)
                .userName("testuser")
                .type("LOGIN")
                .content("用户登录")
                .status("ACTIVE")
                .build();

        when(activityService.searchActivities(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(
                        List.of(mockActivity), PageRequest.of(0, 10), 1));
        when(activityService.getRecentActivitiesByType(anyString()))
                .thenReturn(List.of(mockActivity));
        when(activityService.countActivitiesByUserSince(any(), any()))
                .thenReturn(10L);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testListActivities_Success() throws Exception {
        mockMvc.perform(get("/activity/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testListActivities_WithFilters() throws Exception {
        mockMvc.perform(get("/activity/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("type", "LOGIN")
                        .param("status", "ACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void testGetRecentActivities_Success() throws Exception {
        mockMvc.perform(get("/activity/recent")
                        .param("type", "LOGIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].type").value("LOGIN"));
    }

    @Test
    void testGetRelatedActivities_Success() throws Exception {
        mockMvc.perform(get("/activity/related")
                        .param("relatedType", "CASE")
                        .param("relatedId", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testCountActivities_Success() throws Exception {
        mockMvc.perform(get("/activity/count")
                        .param("days", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    void testCountActivities_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/activity/count")
                        .param("days", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未登录"));
    }
}
