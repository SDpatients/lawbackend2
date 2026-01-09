package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.Activity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {

    Activity recordActivity(Long userId, String userName, String type, String content, String relatedType, Long relatedId);

    Page<Activity> getUserActivities(Long userId, Pageable pageable);

    Page<Activity> searchActivities(Long userId, String type, String status, Pageable pageable);

    List<Activity> getRecentActivitiesByType(String type);

    List<Activity> getActivitiesByRelatedTypeAndRelatedId(String relatedType, Long relatedId);

    long countActivitiesByUserSince(Long userId, java.time.LocalDateTime startTime);
}
