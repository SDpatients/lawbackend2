package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CaseAnnouncementRepository extends JpaRepository<CaseAnnouncement, Long> {

    Page<CaseAnnouncement> findByCaseId(Long caseId, Pageable pageable);

    Page<CaseAnnouncement> findByCaseIdAndStatus(Long caseId, String status, Pageable pageable);

    Page<CaseAnnouncement> findByStatus(String status, Pageable pageable);

    List<CaseAnnouncement> findByIsTopTrueAndTopExpireTimeBefore(LocalDateTime expireTime);
    void deleteByCaseId(Long caseId);
}
