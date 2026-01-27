package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseTaskSubmissionRepository extends JpaRepository<CaseTaskSubmission, Long> {

    List<CaseTaskSubmission> findByCaseTaskId(Long caseTaskId);

    Page<CaseTaskSubmission> findByCaseTaskId(Long caseTaskId, Pageable pageable);

    Optional<CaseTaskSubmission> findFirstByCaseTaskIdOrderBySubmissionNumberDesc(Long caseTaskId);

    @Query("SELECT s FROM CaseTaskSubmission s WHERE s.caseTaskId = :caseTaskId AND s.isDeleted = false ORDER BY s.submissionNumber DESC")
    List<CaseTaskSubmission> findLatestByCaseTaskId(@Param("caseTaskId") Long caseTaskId);

    @Query("SELECT COUNT(s) FROM CaseTaskSubmission s WHERE s.caseTaskId = :caseTaskId AND s.status = :status AND s.isDeleted = false")
    Long countByCaseTaskIdAndStatus(@Param("caseTaskId") Long caseTaskId, @Param("status") String status);

    @Query("SELECT MAX(s.submissionNumber) FROM CaseTaskSubmission s WHERE s.caseTaskId = :caseTaskId AND s.isDeleted = false")
    Integer getMaxSubmissionNumberByCaseTaskId(@Param("caseTaskId") Long caseTaskId);

    @Query("SELECT s FROM CaseTaskSubmission s WHERE s.caseTaskId IN (SELECT ct.id FROM CaseTask ct WHERE ct.caseId = :caseId) AND s.isDeleted = false")
    List<CaseTaskSubmission> findByCaseId(@Param("caseId") Long caseId);

    @Modifying
    @Query("DELETE FROM CaseTaskSubmission s WHERE s.caseTaskId IN (SELECT ct.id FROM CaseTask ct WHERE ct.caseId = :caseId)")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT s FROM CaseTaskSubmission s WHERE s.caseTaskId IN :caseTaskIds AND s.isDeleted = false ORDER BY s.caseTaskId, s.submissionNumber DESC")
    List<CaseTaskSubmission> findLatestByCaseTaskIds(@Param("caseTaskIds") List<Long> caseTaskIds);
}
