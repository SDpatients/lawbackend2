package com.lawbackend2.lawbackend2.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCache;

@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("缓存读取失败 - cache: {}, key: {}, error: {}", cache.getName(), key, exception.getMessage());
        
        if (isDeserializationError(exception)) {
            log.info("检测到缓存数据格式不兼容，自动清除缓存: {}", cache.getName());
            try {
                cache.clear();
                log.info("缓存清除成功: {}", cache.getName());
            } catch (Exception e) {
                log.error("清除缓存失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.error("缓存写入失败 - cache: {}, key: {}, error: {}", cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("缓存删除失败 - cache: {}, key: {}, error: {}", cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("缓存清空失败 - cache: {}, error: {}", cache.getName(), exception.getMessage());
    }

    private boolean isDeserializationError(RuntimeException exception) {
        Throwable cause = exception;
        while (cause != null) {
            String className = cause.getClass().getName();
            if (className.contains("ClassCastException") ||
                className.contains("SerializationException") ||
                className.contains("JsonMappingException") ||
                className.contains("JsonParseException") ||
                className.contains("InvalidTypeIdException") ||
                className.contains("UnrecognizedPropertyException")) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
