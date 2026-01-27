package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseTaskRepository extends JpaRepository<CaseTask, Long> {

    List<CaseTask> findByCaseId(Long caseId);

    List<CaseTask> findByCaseIdAndStatus(Long caseId, String status);

    Optional<CaseTask> findByCaseIdAndTaskCode(Long caseId, String taskCode);

    Page<CaseTask> findByCaseId(Long caseId, Pageable pageable);

    @Query("SELECT ct FROM CaseTask ct WHERE ct.caseId = :caseId AND ct.isDeleted = false ORDER BY ct.sortOrder")
    List<CaseTask> findByCaseIdOrderBySortOrder(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(ct) FROM CaseTask ct WHERE ct.caseId = :caseId AND ct.status = :status AND ct.isDeleted = false")
    Long countByCaseIdAndStatus(@Param("caseId") Long caseId, @Param("status") String status);
    void deleteByCaseId(Long caseId);
}
