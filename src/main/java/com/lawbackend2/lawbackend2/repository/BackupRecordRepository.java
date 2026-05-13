package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.BackupRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackupRecordRepository extends JpaRepository<BackupRecord, Long> {

    Page<BackupRecord> findByStatus(String status, Pageable pageable);

    Page<BackupRecord> findByBackupType(String backupType, Pageable pageable);

    Optional<BackupRecord> findFirstByStatusOrderByStartTimeDesc(String status);

    Optional<BackupRecord> findFirstByOrderByStartTimeDesc();

    @Query("SELECT br FROM BackupRecord br WHERE br.status = :status AND br.isDeleted = false ORDER BY br.startTime DESC")
    List<BackupRecord> findByStatusAndNotDeleted(@Param("status") String status);

    @Query("SELECT br FROM BackupRecord br WHERE br.isDeleted = false ORDER BY br.startTime DESC")
    Page<BackupRecord> findAllNotDeleted(Pageable pageable);

    @Query("SELECT br FROM BackupRecord br WHERE br.createTime < :threshold AND br.isDeleted = false")
    List<BackupRecord> findByCreateTimeBeforeAndNotDeleted(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT COUNT(br) FROM BackupRecord br WHERE br.status = :status AND br.isDeleted = false")
    long countByStatusAndNotDeleted(@Param("status") String status);

    boolean existsByStatus(String status);

    @Query("SELECT br FROM BackupRecord br WHERE br.isDeleted = false " +
           "AND (:status IS NULL OR br.status = :status) " +
           "AND (:backupType IS NULL OR br.backupType = :backupType) " +
           "AND (:startDate IS NULL OR br.startTime >= :startDate) " +
           "AND (:endDate IS NULL OR br.startTime <= :endDate) " +
           "ORDER BY br.startTime DESC")
    Page<BackupRecord> findAllWithFilters(@Param("status") String status,
                                          @Param("backupType") String backupType,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    @Query("SELECT COALESCE(SUM(br.fileSize), 0) FROM BackupRecord br WHERE br.isDeleted = false AND br.status = 'SUCCESS'")
    long sumSuccessFileSize();

    @Query("SELECT br FROM BackupRecord br WHERE br.isDeleted = false AND br.status = 'SUCCESS' ORDER BY br.startTime DESC")
    List<BackupRecord> findLatestSuccessBackup();

    @Query("SELECT COUNT(br) FROM BackupRecord br WHERE br.isDeleted = false")
    long countAllNotDeleted();
}
