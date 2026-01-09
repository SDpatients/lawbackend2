package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.Activity;
import com.lawbackend2.lawbackend2.repository.ActivityRepository;
import com.lawbackend2.lawbackend2.service.ActivityService;
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
class ActivityServiceImplTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityServiceImpl activityService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_NAME = "testuser";
    private static final String TEST_TYPE = "LOGIN";
    private static final String TEST_CONTENT = "用户登录";
    private static final Long TEST_ACTIVITY_ID = 1L;

    @BeforeEach
    void setUp() {
        Activity mockActivity = Activity.builder()
                .id(TEST_ACTIVITY_ID)
                .userId(TEST_USER_ID)
                .userName(TEST_USER_NAME)
                .type(TEST_TYPE)
                .content(TEST_CONTENT)
                .status("ACTIVE")
                .relatedType("CASE")
                .relatedId(100L)
                .build();

        when(activityRepository.save(any(Activity.class))).thenReturn(mockActivity);
        when(activityRepository.findByUserId(eq(TEST_USER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockActivity), PageRequest.of(0, 10), 1));
        when(activityRepository.searchActivities(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockActivity), PageRequest.of(0, 10), 1));
        when(activityRepository.findRecentActivitiesByType(anyString(), any(LocalDateTime.class)))
                .thenReturn(List.of(mockActivity));
        when(activityRepository.findByRelatedTypeAndRelatedId(anyString(), anyLong()))
                .thenReturn(List.of(mockActivity));
        when(activityRepository.countActivitiesByUserSince(eq(TEST_USER_ID), any(LocalDateTime.class)))
                .thenReturn(10L);
    }

    @Test
    void testRecordActivity_Success() {
        Activity activity = activityService.recordActivity(
                TEST_USER_ID, TEST_USER_NAME, TEST_TYPE, TEST_CONTENT, "CASE", 100L);

        assertNotNull(activity);
        assertEquals(TEST_USER_ID, activity.getUserId());
        assertEquals(TEST_TYPE, activity.getType());
        assertEquals(TEST_CONTENT, activity.getContent());
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void testGetUserActivities_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Activity> page = activityService.getUserActivities(TEST_USER_ID, pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals(TEST_TYPE, page.getContent().get(0).getType());
        verify(activityRepository, times(1)).findByUserId(TEST_USER_ID, pageable);
    }

    @Test
    void testSearchActivities_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Activity> page = activityService.searchActivities(
                TEST_USER_ID, TEST_TYPE, "ACTIVE", pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(activityRepository, times(1)).searchActivities(TEST_USER_ID, TEST_TYPE, "ACTIVE", pageable);
    }

    @Test
    void testGetRecentActivitiesByType_Success() {
        List<Activity> activities = activityService.getRecentActivitiesByType(TEST_TYPE);

        assertNotNull(activities);
        assertEquals(1, activities.size());
        assertEquals(TEST_TYPE, activities.get(0).getType());
        verify(activityRepository, times(1)).findRecentActivitiesByType(eq(TEST_TYPE), any(LocalDateTime.class));
    }

    @Test
    void testGetActivitiesByRelatedTypeAndRelatedId_Success() {
        List<Activity> activities = activityService.getActivitiesByRelatedTypeAndRelatedId("CASE", 100L);

        assertNotNull(activities);
        assertEquals(1, activities.size());
        assertEquals("CASE", activities.get(0).getRelatedType());
        assertEquals(100L, activities.get(0).getRelatedId());
        verify(activityRepository, times(1)).findByRelatedTypeAndRelatedId("CASE", 100L);
    }

    @Test
    void testCountActivitiesByUserSince_Success() {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        long count = activityService.countActivitiesByUserSince(TEST_USER_ID, startTime);

        assertEquals(10L, count);
        verify(activityRepository, times(1)).countActivitiesByUserSince(TEST_USER_ID, startTime);
    }

    @Test
    void testRecordActivity_AllParameters() {
        Activity activity = activityService.recordActivity(
                TEST_USER_ID, 
                TEST_USER_NAME, 
                TEST_TYPE, 
                "创建案件", 
                "CASE", 
                100L
        );

        assertNotNull(activity);
        assertEquals("CASE", activity.getRelatedType());
        assertEquals(100L, activity.getRelatedId());
        assertEquals("创建案件", activity.getContent());
    }
}
