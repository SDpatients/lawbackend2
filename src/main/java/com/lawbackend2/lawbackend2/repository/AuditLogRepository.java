package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    Page<AuditLog> findByModule(String module, Pageable pageable);

    Page<AuditLog> findByOperationType(String operationType, Pageable pageable);

    Page<AuditLog> findByStatus(String status, Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE al.businessType = :businessType AND al.businessId = :businessId")
    Page<AuditLog> findByBusinessTypeAndBusinessId(@Param("businessType") String businessType, 
                                                     @Param("businessId") Long businessId, 
                                                     Pageable pageable);

    @Query("SELECT al FROM AuditLog al WHERE " +
           "(:userId IS NULL OR al.userId = :userId) AND " +
           "(:module IS NULL OR al.module = :module) AND " +
           "(:operationType IS NULL OR al.operationType = :operationType) AND " +
           "(:status IS NULL OR al.status = :status) AND " +
           "(:startTime IS NULL OR al.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR al.createTime <= :endTime) AND " +
           "(:keyword IS NULL OR al.businessName LIKE %:keyword% OR al.userName LIKE %:keyword% OR al.moduleName LIKE %:keyword%)")
    Page<AuditLog> searchAuditLogs(
            @Param("userId") Long userId,
            @Param("module") String module,
            @Param("operationType") String operationType,
            @Param("status") String status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.createTime BETWEEN :startTime AND :endTime")
    long countByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT al.module, al.moduleName, COUNT(al) as cnt FROM AuditLog al WHERE al.createTime BETWEEN :startTime AND :endTime GROUP BY al.module, al.moduleName")
    List<Object[]> countGroupByModule(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT al.operationType, al.operationName, COUNT(al) as cnt FROM AuditLog al WHERE al.createTime BETWEEN :startTime AND :endTime GROUP BY al.operationType, al.operationName")
    List<Object[]> countGroupByOperationType(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT DATE(al.createTime) as date, COUNT(al) as cnt FROM AuditLog al WHERE al.createTime BETWEEN :startTime AND :endTime GROUP BY DATE(al.createTime) ORDER BY DATE(al.createTime)")
    List<Object[]> countGroupByDate(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
