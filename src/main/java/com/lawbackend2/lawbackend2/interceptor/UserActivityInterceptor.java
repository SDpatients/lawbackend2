package com.lawbackend2.lawbackend2.interceptor;

import com.lawbackend2.lawbackend2.util.SecurityUtil;
import com.lawbackend2.lawbackend2.util.UserActivityTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class UserActivityInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            UserActivityTracker.recordActivity(userId);
        } catch (Exception e) {
        }
    }
}