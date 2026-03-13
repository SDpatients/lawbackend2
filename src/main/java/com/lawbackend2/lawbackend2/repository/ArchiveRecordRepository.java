package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ArchiveRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArchiveRecordRepository extends JpaRepository<ArchiveRecord, Long> {

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId")
    List<ArchiveRecord> findByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId")
    Page<ArchiveRecord> findByCaseIdWithPage(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId AND ar.status = :status")
    Page<ArchiveRecord> findByCaseIdAndStatus(@Param("caseId") Long caseId, @Param("status") String status, Pageable pageable);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId AND ar.categoryCode = :categoryCode AND ar.status = :status")
    Page<ArchiveRecord> findByCaseIdAndCategoryCodeAndStatus(@Param("caseId") Long caseId, @Param("categoryCode") String categoryCode, @Param("status") String status, Pageable pageable);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId AND ar.status = :status " +
           "AND (ar.fileTitle LIKE %:keyword% OR ar.fileDescription LIKE %:keyword%)")
    Page<ArchiveRecord> findByCaseIdAndStatusAndKeyword(@Param("caseId") Long caseId,
                                                          @Param("status") String status,
                                                          @Param("keyword") String keyword,
                                                          Pageable pageable);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId AND ar.categoryCode = :categoryCode " +
           "AND ar.status = :status AND (ar.fileTitle LIKE %:keyword% OR ar.fileDescription LIKE %:keyword%)")
    Page<ArchiveRecord> findByCaseIdAndCategoryCodeAndStatusAndKeyword(@Param("caseId") Long caseId,
                                                                        @Param("categoryCode") String categoryCode,
                                                                        @Param("status") String status,
                                                                        @Param("keyword") String keyword,
                                                                        Pageable pageable);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.archiveNo = :archiveNo")
    ArchiveRecord findByArchiveNo(@Param("archiveNo") String archiveNo);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.fileId = :fileId")
    List<ArchiveRecord> findByFileId(@Param("fileId") Long fileId);

    @Query("SELECT ar FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.uploadTime BETWEEN :startTime AND :endTime")
    List<ArchiveRecord> findByUploadTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(ar) FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId AND ar.status = :status")
    Long countByCaseIdAndStatus(@Param("caseId") Long caseId, @Param("status") String status);

    @Query("SELECT COUNT(ar) FROM ArchiveRecord ar WHERE ar.isDeleted = false AND ar.caseId = :caseId AND ar.categoryCode = :categoryCode AND ar.status = :status")
    Long countByCaseIdAndCategoryCodeAndStatus(@Param("caseId") Long caseId,
                                               @Param("categoryCode") String categoryCode,
                                               @Param("status") String status);

    @Modifying
    @Query("UPDATE ArchiveRecord ar SET ar.isDeleted = true WHERE ar.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
