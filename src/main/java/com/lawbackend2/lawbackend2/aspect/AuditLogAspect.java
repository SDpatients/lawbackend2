package com.lawbackend2.lawbackend2.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.AuditLogService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.lawbackend2.lawbackend2.annotation.AuditLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        AuditLog auditLogAnnotation = method.getAnnotation(AuditLog.class);

        HttpServletRequest request = getRequest();
        
        com.lawbackend2.lawbackend2.entity.AuditLog.AuditLogBuilder logBuilder = com.lawbackend2.lawbackend2.entity.AuditLog.builder()
                .module(auditLogAnnotation.module())
                .moduleName(auditLogAnnotation.moduleName())
                .operationType(auditLogAnnotation.operationType())
                .operationName(auditLogAnnotation.operationName())
                .businessType(auditLogAnnotation.businessType())
                .createTime(LocalDateTime.now());

        if (request != null) {
            logBuilder.requestMethod(request.getMethod())
                    .requestUrl(request.getRequestURI())
                    .ipAddress(getClientIp(request))
                    .browser(getBrowser(request))
                    .os(getOs(request));

            if (auditLogAnnotation.recordParams()) {
                try {
                    String params = getRequestParams(point, signature);
                    logBuilder.requestParams(params);
                } catch (Exception e) {
                    log.debug("Failed to serialize request params", e);
                }
            }
        }

        try {
            Long userId = SecurityUtil.getCurrentUserId();
            if (userId != null) {
                logBuilder.userId(userId);
                String username = SecurityUtil.getCurrentUsername();
                if (username != null) {
                    logBuilder.userAccount(username);
                }
            }
        } catch (Exception e) {
            log.debug("无法获取当前用户ID，可能是未认证的公开接口");
        }

        Object result = null;
        Exception exception = null;

        try {
            result = point.proceed();
            logBuilder.status(com.lawbackend2.lawbackend2.entity.AuditLog.STATUS_SUCCESS);
            
            if (auditLogAnnotation.recordData() && result != null) {
                try {
                    logBuilder.dataAfter(objectMapper.writeValueAsString(result));
                } catch (Exception e) {
                    log.debug("Failed to serialize result", e);
                }
            }
        } catch (Exception e) {
            exception = e;
            logBuilder.status(com.lawbackend2.lawbackend2.entity.AuditLog.STATUS_FAIL)
                    .errorMessage(e.getMessage());
        }

        long duration = System.currentTimeMillis() - startTime;
        logBuilder.duration(duration);

        try {
            auditLogService.save(logBuilder.build());
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }

        if (exception != null) {
            throw exception;
        }

        return result;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edge")) return "Edge";
        if (userAgent.contains("chrome")) return "Chrome";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("safari")) return "Safari";
        if (userAgent.contains("opera")) return "Opera";
        if (userAgent.contains("msie") || userAgent.contains("trident")) return "IE";
        return "Unknown";
    }

    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac")) return "Mac OS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone") || userAgent.contains("ipad")) return "iOS";
        return "Unknown";
    }

    private String getRequestParams(ProceedingJoinPoint point, MethodSignature signature) {
        try {
            String[] paramNames = signature.getParameterNames();
            Object[] args = point.getArgs();
            
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                Object arg = args[i];
                if (arg instanceof HttpServletRequest 
                    || arg instanceof HttpServletResponse 
                    || arg instanceof MultipartFile) {
                    continue;
                }
                params.put(paramNames[i], arg);
            }
            
            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            return "{}";
        }
    }
}
