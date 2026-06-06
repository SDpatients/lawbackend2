package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.entity.AuditLog;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.repository.AuditLogRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.AuditLogService;
import com.lawbackend2.lawbackend2.util.HashChainUtil;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private final HashChainUtil hashChainUtil;

    @Value("${audit.hash-chain.private-key:lawbackend2-private-key}")
    private String privateKey;

    @Override
    @Transactional
    public AuditLog save(AuditLog auditLog) {
        enrichAuditLog(auditLog);
        AuditLog savedLog = auditLogRepository.save(auditLog);

        AuditLog lastLog = auditLogRepository.findTopByOrderByIdDesc().orElse(null);
        String previousHash = hashChainUtil.generateGenesisHash();

        if (lastLog != null) {
            if (!lastLog.getId().equals(savedLog.getId())) {
                previousHash = lastLog.getHashValue() != null ? lastLog.getHashValue() : hashChainUtil.generateGenesisHash();
            } else if (lastLog.getId() > 1) {
                AuditLog previousLog = auditLogRepository.findTopByIdLessThanOrderByIdDesc(savedLog.getId()).orElse(null);
                previousHash = (previousLog != null && previousLog.getHashValue() != null) 
                    ? previousLog.getHashValue() 
                    : hashChainUtil.generateGenesisHash();
            }
        }

        String hashValue = hashChainUtil.generateHash(savedLog, previousHash);
        String digitalSignature = hashChainUtil.generateDigitalSignature(savedLog, privateKey);
        String chainSequence = hashChainUtil.generateChainSequence(lastLog);

        savedLog.setHashValue(hashValue);
        savedLog.setPreviousHash(previousHash);
        savedLog.setDigitalSignature(digitalSignature);
        savedLog.setChainSequence(Long.parseLong(chainSequence));
        savedLog.setIntegrityStatus(AuditLog.INTEGRITY_VERIFIED);

        return auditLogRepository.save(savedLog);
    }

    @Override
    public AuditLog getById(Long id) {
        return auditLogRepository.findById(id).orElse(null);
    }

    @Override
    public Page<AuditLog> search(Long userId, String module, String operationType, String status, String integrityStatus,
                                  LocalDateTime startTime, LocalDateTime endTime, String keyword, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(userId, module, operationType, status, integrityStatus, startTime, endTime, keyword, pageable);
    }

    @Override
    public Page<AuditLog> advancedSearch(String userAccount, Long businessId, String ipAddress, String requestUrl, Pageable pageable) {
        return auditLogRepository.advancedSearch(userAccount, businessId, ipAddress, requestUrl, pageable);
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
            AuditLog savedLog = auditLogRepository.save(auditLog);

            AuditLog lastLog = auditLogRepository.findTopByOrderByIdDesc().orElse(null);
            String previousHash = hashChainUtil.generateGenesisHash();

            if (lastLog != null) {
                if (!lastLog.getId().equals(savedLog.getId())) {
                    previousHash = lastLog.getHashValue() != null ? lastLog.getHashValue() : hashChainUtil.generateGenesisHash();
                } else if (lastLog.getId() > 1) {
                    AuditLog previousLog = auditLogRepository.findTopByIdLessThanOrderByIdDesc(savedLog.getId()).orElse(null);
                    previousHash = (previousLog != null && previousLog.getHashValue() != null) 
                        ? previousLog.getHashValue() 
                        : hashChainUtil.generateGenesisHash();
                }
            }

            String hashValue = hashChainUtil.generateHash(savedLog, previousHash);
            String digitalSignature = hashChainUtil.generateDigitalSignature(savedLog, privateKey);
            String chainSequence = hashChainUtil.generateChainSequence(lastLog);

            savedLog.setHashValue(hashValue);
            savedLog.setPreviousHash(previousHash);
            savedLog.setDigitalSignature(digitalSignature);
            savedLog.setChainSequence(Long.parseLong(chainSequence));
            savedLog.setIntegrityStatus(AuditLog.INTEGRITY_VERIFIED);

            auditLogRepository.save(savedLog);
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

    @Override
    @Transactional
    public boolean verifyIntegrity(Long auditLogId) {
        AuditLog auditLog = auditLogRepository.findById(auditLogId).orElse(null);
        if (auditLog == null) {
            return false;
        }

        if (auditLog.getHashValue() == null || auditLog.getHashValue().isEmpty()) {
            auditLog.setIntegrityStatus(AuditLog.INTEGRITY_PENDING);
            auditLogRepository.save(auditLog);
            return true;
        }

        boolean hashValid = hashChainUtil.verifyLogIntegrity(auditLog);
        if (!hashValid) {
            auditLog.setIntegrityStatus(AuditLog.INTEGRITY_TAMPERED);
            auditLogRepository.save(auditLog);
            return false;
        }

        AuditLog previousLog = null;
        if (auditLog.getId() > 1) {
            previousLog = auditLogRepository.findTopByIdLessThanOrderByIdDesc(auditLog.getId()).orElse(null);
        }

        boolean chainValid = hashChainUtil.verifyHashChain(auditLog, previousLog);
        if (!chainValid) {
            auditLog.setIntegrityStatus(AuditLog.INTEGRITY_TAMPERED);
            auditLogRepository.save(auditLog);
            return false;
        }

        auditLog.setIntegrityStatus(AuditLog.INTEGRITY_VERIFIED);
        auditLogRepository.save(auditLog);
        return true;
    }

    @Override
    @Transactional
    public List<AuditLog> verifyAllIntegrity() {
        List<AuditLog> tamperedLogs = new ArrayList<>();
        List<AuditLog> allLogs = auditLogRepository.findAllByOrderByIdAsc();

        for (int i = 0; i < allLogs.size(); i++) {
            AuditLog currentLog = allLogs.get(i);

            if (currentLog.getHashValue() == null || currentLog.getHashValue().isEmpty()) {
                currentLog.setIntegrityStatus(AuditLog.INTEGRITY_PENDING);
                auditLogRepository.save(currentLog);
                continue;
            }

            boolean logIntegrityValid = hashChainUtil.verifyLogIntegrity(currentLog);

            AuditLog previousLog = (i > 0) ? allLogs.get(i - 1) : null;
            boolean chainValid = hashChainUtil.verifyHashChain(currentLog, previousLog);

            if (!logIntegrityValid) {
                currentLog.setIntegrityStatus(AuditLog.INTEGRITY_TAMPERED);
                auditLogRepository.save(currentLog);
                tamperedLogs.add(currentLog);
            } else if (!chainValid && previousLog != null) {
                boolean prevTmp = AuditLog.INTEGRITY_TAMPERED.equals(previousLog.getIntegrityStatus());
                if (prevTmp) {
                    currentLog.setIntegrityStatus(AuditLog.INTEGRITY_TAMPERED);
                    auditLogRepository.save(currentLog);
                    tamperedLogs.add(currentLog);
                }
            } else {
                currentLog.setIntegrityStatus(AuditLog.INTEGRITY_VERIFIED);
                auditLogRepository.save(currentLog);
            }
        }

        return tamperedLogs;
    }

    @Override
    public Map<String, Object> getIntegrityReport() {
        Map<String, Object> report = new HashMap<>();
        List<AuditLog> allLogs = auditLogRepository.findAll();

        long totalCount = allLogs.size();
        long verifiedCount = allLogs.stream()
                .filter(log -> AuditLog.INTEGRITY_VERIFIED.equals(log.getIntegrityStatus()))
                .count();
        long tamperedCount = allLogs.stream()
                .filter(log -> AuditLog.INTEGRITY_TAMPERED.equals(log.getIntegrityStatus()))
                .count();
        long pendingCount = allLogs.stream()
                .filter(log -> AuditLog.INTEGRITY_PENDING.equals(log.getIntegrityStatus()))
                .count();

        report.put("totalCount", totalCount);
        report.put("verifiedCount", verifiedCount);
        report.put("tamperedCount", tamperedCount);
        report.put("pendingCount", pendingCount);
        report.put("integrityRate", totalCount > 0 ? (double) verifiedCount / (totalCount - pendingCount) * 100 : 100.0);
        report.put("lastVerificationTime", LocalDateTime.now());

        return report;
    }

    @Override
    @Transactional
    public Map<String, Object> migrateAndGenerateHashChain() {
        Map<String, Object> result = new HashMap<>();
        long successCount = 0;
        long skipCount = 0;

        List<AuditLog> allLogs = auditLogRepository.findAllByOrderByIdAsc();
        String previousHash = hashChainUtil.generateGenesisHash();

        for (int i = 0; i < allLogs.size(); i++) {
            AuditLog currentLog = allLogs.get(i);

            try {
                if (i == 0) {
                    previousHash = hashChainUtil.generateGenesisHash();
                } else {
                    AuditLog prev = allLogs.get(i - 1);
                    if (prev.getHashValue() != null && !prev.getHashValue().isEmpty()) {
                        previousHash = prev.getHashValue();
                    }
                }

                String hashValue = hashChainUtil.generateHash(currentLog, previousHash);
                String digitalSignature = hashChainUtil.generateDigitalSignature(currentLog, privateKey);
                String chainSequence = String.valueOf(i + 1);

                currentLog.setHashValue(hashValue);
                currentLog.setPreviousHash(previousHash);
                currentLog.setDigitalSignature(digitalSignature);
                currentLog.setChainSequence(Long.parseLong(chainSequence));
                currentLog.setIntegrityStatus(AuditLog.INTEGRITY_VERIFIED);

                auditLogRepository.save(currentLog);
                successCount++;

                previousHash = hashValue;

                if (successCount % 100 == 0) {
                    log.info("已处理 {} 条审计日志的哈希链生成", successCount);
                }

            } catch (Exception e) {
                log.error("处理审计日志 ID {} 时发生错误: {}", currentLog.getId(), e.getMessage());
            }
        }

        result.put("totalProcessed", successCount);
        result.put("successCount", successCount);
        result.put("skipCount", skipCount);
        result.put("message", String.format("迁移完成！成功处理 %d 条哈希链", successCount));

        return result;
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
