package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseTask;
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
public interface CaseTaskRepository extends JpaRepository<CaseTask, Long> {

    @Query("SELECT ct FROM CaseTask ct WHERE ct.isDeleted = false AND ct.caseId = :caseId")
    List<CaseTask> findByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT ct FROM CaseTask ct WHERE ct.isDeleted = false AND ct.caseId = :caseId AND ct.status = :status")
    List<CaseTask> findByCaseIdAndStatus(@Param("caseId") Long caseId, @Param("status") String status);

    @Query("SELECT ct FROM CaseTask ct WHERE ct.isDeleted = false AND ct.caseId = :caseId AND ct.taskCode = :taskCode")
    Optional<CaseTask> findByCaseIdAndTaskCode(@Param("caseId") Long caseId, @Param("taskCode") String taskCode);

    @Query("SELECT ct FROM CaseTask ct WHERE ct.isDeleted = false AND ct.caseId = :caseId")
    Page<CaseTask> findByCaseIdWithPage(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT ct FROM CaseTask ct WHERE ct.caseId = :caseId AND ct.isDeleted = false ORDER BY ct.sortOrder")
    List<CaseTask> findByCaseIdOrderBySortOrder(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(ct) FROM CaseTask ct WHERE ct.caseId = :caseId AND ct.status = :status AND ct.isDeleted = false")
    Long countByCaseIdAndStatus(@Param("caseId") Long caseId, @Param("status") String status);

    @Modifying
    @Query("UPDATE CaseTask ct SET ct.isDeleted = true WHERE ct.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
