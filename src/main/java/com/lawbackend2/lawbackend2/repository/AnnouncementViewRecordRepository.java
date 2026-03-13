package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.AnnouncementViewRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementViewRecordRepository extends JpaRepository<AnnouncementViewRecord, Long>, JpaSpecificationExecutor<AnnouncementViewRecord> {
    Page<AnnouncementViewRecord> findByAnnouncementId(Long announcementId, Pageable pageable);

    Page<AnnouncementViewRecord> findByCaseId(Long caseId, Pageable pageable);

    Page<AnnouncementViewRecord> findByViewerId(Long viewerId, Pageable pageable);

    Page<AnnouncementViewRecord> findByAnnouncementIdAndCaseId(Long announcementId, Long caseId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM AnnouncementViewRecord r WHERE r.announcementId = :announcementId")
    Long countByAnnouncementId(@Param("announcementId") Long announcementId);

    @Query("SELECT COUNT(r) FROM AnnouncementViewRecord r WHERE r.caseId = :caseId")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(r) FROM AnnouncementViewRecord r WHERE r.viewerId = :viewerId")
    Long countByViewerId(@Param("viewerId") Long viewerId);

    @Query("SELECT COUNT(r) FROM AnnouncementViewRecord r WHERE r.viewTime BETWEEN :startTime AND :endTime")
    Long countByViewTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r.viewerType, COUNT(r) FROM AnnouncementViewRecord r WHERE r.announcementId = :announcementId GROUP BY r.viewerType")
    List<Object[]> countByViewerTypeGroup(@Param("announcementId") Long announcementId);

    @Query("SELECT r.deviceType, COUNT(r) FROM AnnouncementViewRecord r WHERE r.announcementId = :announcementId GROUP BY r.deviceType")
    List<Object[]> countByDeviceTypeGroup(@Param("announcementId") Long announcementId);

    @Query("SELECT r.browserType, COUNT(r) FROM AnnouncementViewRecord r WHERE r.announcementId = :announcementId GROUP BY r.browserType")
    List<Object[]> countByBrowserTypeGroup(@Param("announcementId") Long announcementId);

    @Query("SELECT r.osType, COUNT(r) FROM AnnouncementViewRecord r WHERE r.announcementId = :announcementId GROUP BY r.osType")
    List<Object[]> countByOsTypeGroup(@Param("announcementId") Long announcementId);

    @Query("SELECT AVG(r.viewDuration) FROM AnnouncementViewRecord r WHERE r.announcementId = :announcementId")
    Double getAverageViewDuration(@Param("announcementId") Long announcementId);

    @Query("SELECT r FROM AnnouncementViewRecord r WHERE r.announcementId = :announcementId ORDER BY r.viewTime DESC")
    List<AnnouncementViewRecord> findLatestByAnnouncementId(@Param("announcementId") Long announcementId, Pageable pageable);

    @Modifying
    @Query("UPDATE AnnouncementViewRecord r SET r.isDeleted = true WHERE r.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
