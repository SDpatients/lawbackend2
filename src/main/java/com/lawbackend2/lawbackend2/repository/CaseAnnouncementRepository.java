package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
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
public interface CaseAnnouncementRepository extends JpaRepository<CaseAnnouncement, Long> {

    @Query("SELECT c FROM CaseAnnouncement c WHERE c.isDeleted = false AND c.caseId = :caseId")
    Page<CaseAnnouncement> findByCaseId(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT c FROM CaseAnnouncement c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.status = :status")
    Page<CaseAnnouncement> findByCaseIdAndStatus(@Param("caseId") Long caseId, @Param("status") String status, Pageable pageable);

    @Query("SELECT c FROM CaseAnnouncement c WHERE c.isDeleted = false AND c.status = :status")
    Page<CaseAnnouncement> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT c FROM CaseAnnouncement c WHERE c.isDeleted = false AND c.isTop = true AND c.topExpireTime < :expireTime")
    List<CaseAnnouncement> findByIsTopTrueAndTopExpireTimeBefore(@Param("expireTime") LocalDateTime expireTime);

    @Modifying
    @Query("UPDATE CaseAnnouncement c SET c.isDeleted = true WHERE c.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
