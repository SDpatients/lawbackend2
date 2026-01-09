package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.Activity;
import com.lawbackend2.lawbackend2.repository.ActivityRepository;
import com.lawbackend2.lawbackend2.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    @Transactional
    public Activity recordActivity(Long userId, String userName, String type, String content, 
                                 String relatedType, Long relatedId) {
        Activity activity = Activity.builder()
                .userId(userId)
                .userName(userName)
                .type(type)
                .content(content)
                .relatedType(relatedType)
                .relatedId(relatedId)
                .status("ACTIVE")
                .build();

        Activity savedActivity = activityRepository.save(activity);
        log.info("记录操作日志成功 - 用户ID: {}, 操作类型: {}, 关联类型: {}, 关联ID: {}", 
                userId, type, relatedType, relatedId);
        return savedActivity;
    }

    @Override
    public Page<Activity> getUserActivities(Long userId, Pageable pageable) {
        Page<Activity> page = activityRepository.findByUserId(userId, pageable);
        log.debug("查询用户操作日志 - 用户ID: {}, 总数: {}", userId, page.getTotalElements());
        return page;
    }

    @Override
    public Page<Activity> searchActivities(Long userId, String type, String status, Pageable pageable) {
        Page<Activity> page = activityRepository.searchActivities(userId, type, status, pageable);
        log.debug("搜索操作日志 - 用户ID: {}, 类型: {}, 状态: {}, 总数: {}", 
                userId, type, status, page.getTotalElements());
        return page;
    }

    @Override
    public List<Activity> getRecentActivitiesByType(String type) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        List<Activity> activities = activityRepository.findRecentActivitiesByType(type, startTime);
        log.debug("查询最近7天操作日志 - 类型: {}, 数量: {}", type, activities.size());
        return activities;
    }

    @Override
    public List<Activity> getActivitiesByRelatedTypeAndRelatedId(String relatedType, Long relatedId) {
        List<Activity> activities = activityRepository.findByRelatedTypeAndRelatedId(relatedType, relatedId);
        log.debug("查询关联操作日志 - 关联类型: {}, 关联ID: {}, 数量: {}", 
                relatedType, relatedId, activities.size());
        return activities;
    }

    @Override
    public long countActivitiesByUserSince(Long userId, LocalDateTime startTime) {
        long count = activityRepository.countActivitiesByUserSince(userId, startTime);
        log.debug("统计用户操作日志 - 用户ID: {}, 开始时间: {}, 数量: {}", userId, startTime, count);
        return count;
    }
}
