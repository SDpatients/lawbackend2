package com.lawbackend2.lawbackend2.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class UserActivityTracker {

    private static final String USER_ACTIVITY_PREFIX = "user_activity:";
    private static final long ACTIVE_TIMEOUT_MINUTES = 5;

    private static UserActivityTracker instance;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static boolean isUserActive(Long userId) {
        if (userId == null || instance == null || instance.redisTemplate == null) {
            return false;
        }
        try {
            String key = USER_ACTIVITY_PREFIX + userId;
            Boolean exists = instance.redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.warn("检查用户活跃状态失败 - userId: {}, error: {}", userId, e.getMessage());
            return false;
        }
    }

    public static void recordActivity(Long userId) {
        if (userId == null || instance == null || instance.redisTemplate == null) {
            return;
        }
        try {
            String key = USER_ACTIVITY_PREFIX + userId;
            instance.redisTemplate.opsForValue().set(key, System.currentTimeMillis(), ACTIVE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("记录用户活跃时间失败 - userId: {}, error: {}", userId, e.getMessage());
        }
    }
}