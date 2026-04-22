package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.WorkPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long>, JpaSpecificationExecutor<WorkPlan> {

    @Query("SELECT wp FROM WorkPlan wp WHERE " +
           "(:caseId IS NULL OR wp.caseId = :caseId) " +
           "AND (:planType IS NULL OR wp.planType = :planType) " +
           "AND (:executionStatus IS NULL OR wp.executionStatus = :executionStatus) " +
           "AND (:status IS NULL OR wp.status = :status)")
    Page<WorkPlan> findByConditions(@Param("caseId") Long caseId,
                                     @Param("planType") String planType,
                                     @Param("executionStatus") String executionStatus,
                                     @Param("status") String status,
                                     Pageable pageable);

    List<WorkPlan> findByCaseId(Long caseId);

    @Query("SELECT wp FROM WorkPlan wp WHERE wp.caseId = :caseId")
    Page<WorkPlan> findByCaseIdWithPage(@Param("caseId") Long caseId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM WorkPlan wp WHERE wp.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
    
    int countByPlanNumberStartingWith(String prefix);
}
