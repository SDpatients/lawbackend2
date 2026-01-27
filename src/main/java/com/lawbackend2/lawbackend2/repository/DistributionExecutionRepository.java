package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.DistributionExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistributionExecutionRepository extends JpaRepository<DistributionExecution, Long> {

    Optional<DistributionExecution> findByDistributionNoAndIsDeleted(String distributionNo, Boolean isDeleted);

    @Query("SELECT de FROM DistributionExecution de WHERE de.isDeleted = false " +
           "AND (:caseId IS NULL OR de.caseId = :caseId) " +
           "AND (:distributionBatch IS NULL OR de.distributionBatch = :distributionBatch) " +
           "AND (:approvalStatus IS NULL OR de.approvalStatus = :approvalStatus) " +
           "AND (:executionStatus IS NULL OR de.executionStatus = :executionStatus)")
    Page<DistributionExecution> findByConditions(@Param("caseId") Long caseId,
                                              @Param("distributionBatch") String distributionBatch,
                                              @Param("approvalStatus") String approvalStatus,
                                              @Param("executionStatus") String executionStatus,
                                              Pageable pageable);

    List<DistributionExecution> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);
    void deleteByCaseId(Long caseId);
}