package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.entity.AuditLog;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.repository.AuditLogRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.AuditLogService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AuditLog save(AuditLog auditLog) {
        enrichAuditLog(auditLog);
        return auditLogRepository.save(auditLog);
    }

    @Override
    public AuditLog getById(Long id) {
        return auditLogRepository.findById(id).orElse(null);
    }

    @Override
    public Page<AuditLog> search(Long userId, String module, String operationType, String status,
                                  LocalDateTime startTime, LocalDateTime endTime, String keyword, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(userId, module, operationType, status, startTime, endTime, keyword, pageable);
    }

    @Override
    public Page<AuditLog> getByUserId(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<AuditLog> getByBusiness(String businessType, Long businessId, Pageable pageable) {
        return auditLogRepository.findByBusinessTypeAndBusinessId(businessType, businessId, pageable);
    }

    @Override
    public long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.countByCreateTimeBetween(startTime, endTime);
    }

    @Override
    public Map<String, Long> countGroupByModule(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> results = auditLogRepository.countGroupByModule(startTime, endTime);
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] arr : results) {
            String moduleName = (String) arr[1];
            if (moduleName == null || moduleName.isEmpty()) {
                moduleName = (String) arr[0];
            }
            map.put(moduleName, (Long) arr[2]);
        }
        return map;
    }

    @Override
    public Map<String, Long> countGroupByOperationType(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> results = auditLogRepository.countGroupByOperationType(startTime, endTime);
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] arr : results) {
            String operationName = (String) arr[1];
            if (operationName == null || operationName.isEmpty()) {
                operationName = (String) arr[0];
            }
            map.put(operationName, (Long) arr[2]);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> getTrendData(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> results = auditLogRepository.countGroupByDate(startTime, endTime);
        return results.stream().map(arr -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", arr[0]);
            item.put("count", arr[1]);
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void logOperation(String module, String moduleName, String operationType, String operationName,
                             String businessType, Long businessId, String businessName,
                             String dataBefore, String dataAfter, String status, String errorMessage) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            String userAccount = SecurityUtil.getCurrentUsername();
            String userName = null;

            if (userId != null) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    userName = userOpt.get().getRealName();
                }
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .userAccount(userAccount)
                    .userName(userName)
                    .module(module)
                    .moduleName(moduleName)
                    .operationType(operationType)
                    .operationName(operationName)
                    .businessType(businessType)
                    .businessId(businessId)
                    .businessName(businessName)
                    .dataBefore(dataBefore)
                    .dataAfter(dataAfter)
                    .status(status)
                    .errorMessage(errorMessage)
                    .createTime(LocalDateTime.now())
                    .build();

            enrichAuditLog(auditLog);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("保存审计日志失败", e);
        }
    }

    @Override
    public void logSuccess(String module, String moduleName, String operationType, String operationName,
                           String businessType, Long businessId, String businessName) {
        logOperation(module, moduleName, operationType, operationName, businessType, businessId,
                businessName, null, null, AuditLog.STATUS_SUCCESS, null);
    }

    @Override
    public void logFail(String module, String moduleName, String operationType, String operationName,
                        String businessType, Long businessId, String businessName, String errorMessage) {
        logOperation(module, moduleName, operationType, operationName, businessType, businessId,
                businessName, null, null, AuditLog.STATUS_FAIL, errorMessage);
    }

    private void enrichAuditLog(AuditLog auditLog) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                auditLog.setIpAddress(getClientIp(request));
                auditLog.setRequestMethod(request.getMethod());
                auditLog.setRequestUrl(request.getRequestURI());
                auditLog.setBrowser(getBrowser(request));
                auditLog.setOs(getOs(request));
            }
        } catch (Exception e) {
            log.debug("获取请求信息失败: {}", e.getMessage());
        }
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
}
