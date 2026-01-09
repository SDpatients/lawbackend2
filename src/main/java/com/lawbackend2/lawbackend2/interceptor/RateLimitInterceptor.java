package com.lawbackend2.lawbackend2.interceptor;

import com.lawbackend2.lawbackend2.annotation.RateLimit;
import com.lawbackend2.lawbackend2.exception.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimitAnnotation = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimitAnnotation == null) {
            return true;
        }

        String key = buildRateLimitKey(request, rateLimitAnnotation);
        int limit = rateLimitAnnotation.limit();
        long timeout = rateLimitAnnotation.timeout();

        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == null || currentCount == 1) {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }

        if (currentCount != null && currentCount > limit) {
            log.warn("请求频率限制触发 - IP: {}, URI: {}, 计数: {}, 限制: {}", 
                    getClientIp(request), request.getRequestURI(), currentCount, limit);
            throw new RateLimitException(429, "请求过于频繁，请稍后再试");
        }

        return true;
    }

    private String buildRateLimitKey(HttpServletRequest request, RateLimit rateLimitAnnotation) {
        String customKey = rateLimitAnnotation.key();
        if (customKey != null && !customKey.isEmpty()) {
            return RATE_LIMIT_PREFIX + customKey;
        }

        String ip = getClientIp(request);
        String uri = request.getRequestURI();
        return RATE_LIMIT_PREFIX + ip + ":" + uri;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
