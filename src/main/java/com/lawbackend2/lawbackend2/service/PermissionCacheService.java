package com.lawbackend2.lawbackend2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PermissionCacheService {

    private static final String PERMISSION_CACHE_KEY_PREFIX = "user:permissions:";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String getCacheKey(Long userId) {
        return PERMISSION_CACHE_KEY_PREFIX + userId;
    }

    @SuppressWarnings("unchecked")
    public List<String> getUserPermissions(Long userId) {
        try {
            String cacheKey = getCacheKey(userId);
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            
            if (cached != null) {
                log.debug("从缓存获取用户权限 - 用户ID: {}, 权限数量: {}", userId, ((List<String>) cached).size());
                return (List<String>) cached;
            }
            
            log.debug("权限缓存未命中 - 用户ID: {}", userId);
            return null;
        } catch (Exception e) {
            log.warn("从Redis获取权限缓存失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
            return null;
        }
    }

    public void setUserPermissions(Long userId, List<String> permissions) {
        try {
            String cacheKey = getCacheKey(userId);
            redisTemplate.opsForValue().set(cacheKey, permissions, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("缓存用户权限 - 用户ID: {}, 权限数量: {}, 过期时间: {}分钟", userId, permissions.size(), CACHE_EXPIRE_MINUTES);
        } catch (Exception e) {
            log.warn("缓存用户权限到Redis失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    public void evictUserPermissions(Long userId) {
        try {
            String cacheKey = getCacheKey(userId);
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (Boolean.TRUE.equals(deleted)) {
                log.debug("清除用户权限缓存 - 用户ID: {}", userId);
            }
        } catch (Exception e) {
            log.warn("清除用户权限缓存失败 - 用户ID: {}, 错误: {}", userId, e.getMessage());
        }
    }

    public void evictAllUserPermissions() {
        try {
            String pattern = PERMISSION_CACHE_KEY_PREFIX + "*";
            redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Void>) connection -> {
                byte[] patternBytes = pattern.getBytes();
                java.util.Set<byte[]> keys = connection.keys(patternBytes);
                if (keys != null && !keys.isEmpty()) {
                    connection.del(keys.toArray(new byte[0][]));
                    log.info("清除所有用户权限缓存 - 清除数量: {}", keys.size());
                }
                return null;
            });
        } catch (Exception e) {
            log.warn("清除所有用户权限缓存失败 - 错误: {}", e.getMessage());
        }
    }

    public void evictMultipleUserPermissions(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        try {
            for (Long userId : userIds) {
                evictUserPermissions(userId);
            }
            log.debug("批量清除用户权限缓存 - 用户数量: {}", userIds.size());
        } catch (Exception e) {
            log.warn("批量清除用户权限缓存失败 - 错误: {}", e.getMessage());
        }
    }
}
