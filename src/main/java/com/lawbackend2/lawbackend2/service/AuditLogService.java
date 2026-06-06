package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AuditLogService {

    AuditLog save(AuditLog auditLog);

    AuditLog getById(Long id);

    Page<AuditLog> search(Long userId, String module, String operationType, String status, String integrityStatus,
                          LocalDateTime startTime, LocalDateTime endTime, String keyword, Pageable pageable);

    Page<AuditLog> advancedSearch(String userAccount, Long businessId, String ipAddress, String requestUrl, Pageable pageable);

    Page<AuditLog> getByUserId(Long userId, Pageable pageable);

    Page<AuditLog> getByBusiness(String businessType, Long businessId, Pageable pageable);

    long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    Map<String, Long> countGroupByModule(LocalDateTime startTime, LocalDateTime endTime);

    Map<String, Long> countGroupByOperationType(LocalDateTime startTime, LocalDateTime endTime);

    List<Map<String, Object>> getTrendData(LocalDateTime startTime, LocalDateTime endTime);

    void logOperation(String module, String moduleName, String operationType, String operationName,
                      String businessType, Long businessId, String businessName,
                      String dataBefore, String dataAfter, String status, String errorMessage);

    void logSuccess(String module, String moduleName, String operationType, String operationName,
                    String businessType, Long businessId, String businessName);

    void logFail(String module, String moduleName, String operationType, String operationName,
                 String businessType, Long businessId, String businessName, String errorMessage);

    boolean verifyIntegrity(Long auditLogId);

    List<AuditLog> verifyAllIntegrity();

    Map<String, Object> getIntegrityReport();

    Map<String, Object> migrateAndGenerateHashChain();
}
