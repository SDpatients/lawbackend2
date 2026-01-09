package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseProgressRepository extends JpaRepository<CaseProgress, Long>, JpaSpecificationExecutor<CaseProgress> {
    Page<CaseProgress> findByCaseId(Long caseId, Pageable pageable);

    Page<CaseProgress> findByCaseIdOrderByStartDateDesc(Long caseId, Pageable pageable);

    Page<CaseProgress> findByProgressStage(String progressStage, Pageable pageable);

    Page<CaseProgress> findByCaseIdAndProgressStage(Long caseId, String progressStage, Pageable pageable);

    Page<CaseProgress> findByCaseIdAndIsCompleted(Long caseId, Boolean isCompleted, Pageable pageable);

    Page<CaseProgress> findByProgressStatus(String progressStatus, Pageable pageable);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId ORDER BY cp.startDate ASC")
    List<CaseProgress> findByCaseIdOrderByStartDate(@Param("caseId") Long caseId);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId AND cp.isCompleted = false")
    List<CaseProgress> findInProgressByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId AND cp.isCompleted = true")
    List<CaseProgress> findCompletedByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId AND cp.progressStage = :progressStage")
    Optional<CaseProgress> findByCaseIdAndProgressStage(@Param("caseId") Long caseId, @Param("progressStage") String progressStage);

    @Query("SELECT COUNT(cp) FROM CaseProgress cp WHERE cp.caseId = :caseId")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(cp) FROM CaseProgress cp WHERE cp.caseId = :caseId AND cp.isCompleted = true")
    Long countCompletedByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT AVG(cp.completionPercentage) FROM CaseProgress cp WHERE cp.caseId = :caseId")
    Double getAverageCompletionPercentage(@Param("caseId") Long caseId);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.responsiblePersonId = :responsiblePersonId")
    Page<CaseProgress> findByResponsiblePersonId(@Param("responsiblePersonId") Long responsiblePersonId, Pageable pageable);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId ORDER BY cp.createTime DESC")
    List<CaseProgress> findLatestByCaseId(@Param("caseId") Long caseId, Pageable pageable);
}
