package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/cache")
@Tag(name = "缓存管理", description = "缓存管理相关接口")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "清除所有缓存")
    @DeleteMapping("/clear")
    public Result<Void> clearAllCache() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("清除所有缓存成功，共清除 {} 个键", keys.size());
            } else {
                log.info("没有找到需要清除的缓存键");
            }
            return Result.success();
        } catch (Exception e) {
            log.error("清除缓存失败", e);
            return Result.error("清除缓存失败：" + e.getMessage());
        }
    }

    @Operation(summary = "清除指定缓存")
    @DeleteMapping("/clear/{cacheName}")
    public Result<Void> clearCache(@PathVariable String cacheName) {
        try {
            Set<String> keys = redisTemplate.keys(cacheName + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("清除缓存 {} 成功，共清除 {} 个键", cacheName, keys.size());
            } else {
                log.info("缓存 {} 没有找到需要清除的键", cacheName);
            }
            return Result.success();
        } catch (Exception e) {
            log.error("清除缓存 {} 失败", cacheName, e);
            return Result.error("清除缓存失败：" + e.getMessage());
        }
    }
}
