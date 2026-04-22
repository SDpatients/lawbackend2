package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseProgressRepository extends JpaRepository<CaseProgress, Long>, JpaSpecificationExecutor<CaseProgress> {
    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId")
    Page<CaseProgress> findByCaseId(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId ORDER BY cp.startDate DESC")
    Page<CaseProgress> findByCaseIdOrderByStartDateDesc(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.progressStage = :progressStage")
    Page<CaseProgress> findByProgressStage(@Param("progressStage") String progressStage, Pageable pageable);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId AND cp.progressStage = :progressStage")
    Page<CaseProgress> findByCaseIdAndProgressStage(@Param("caseId") Long caseId, @Param("progressStage") String progressStage, Pageable pageable);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.caseId = :caseId AND cp.isCompleted = :isCompleted")
    Page<CaseProgress> findByCaseIdAndIsCompleted(@Param("caseId") Long caseId, @Param("isCompleted") Boolean isCompleted, Pageable pageable);

    @Query("SELECT cp FROM CaseProgress cp WHERE cp.progressStatus = :progressStatus")
    Page<CaseProgress> findByProgressStatus(@Param("progressStatus") String progressStatus, Pageable pageable);

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

    @Modifying
    @Query("DELETE FROM CaseProgress cp WHERE cp.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT cp FROM CaseProgress cp")
    Page<CaseProgress> findAllActive(Pageable pageable);
}
