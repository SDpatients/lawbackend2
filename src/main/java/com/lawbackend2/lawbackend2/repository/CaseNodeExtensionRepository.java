package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CaseNodeExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseNodeExtensionRepository extends JpaRepository<CaseNodeExtension, Long> {

    @Query("SELECT ne FROM CaseNodeExtension ne WHERE ne.nodeInstanceId = :nodeInstanceId AND ne.isDeleted = false ORDER BY ne.applyTime DESC")
    List<CaseNodeExtension> findByNodeInstanceIdOrderByApplyTimeDesc(@Param("nodeInstanceId") Long nodeInstanceId);

    @Query("SELECT ne FROM CaseNodeExtension ne WHERE ne.caseId = :caseId AND ne.isDeleted = false ORDER BY ne.applyTime DESC")
    List<CaseNodeExtension> findByCaseIdOrderByApplyTimeDesc(@Param("caseId") Long caseId);

    @Query("SELECT ne FROM CaseNodeExtension ne WHERE ne.caseId = :caseId AND ne.approvalStatus = :approvalStatus AND ne.isDeleted = false ORDER BY ne.applyTime DESC")
    List<CaseNodeExtension> findByCaseIdAndApprovalStatusOrderByApplyTimeDesc(@Param("caseId") Long caseId, @Param("approvalStatus") String approvalStatus);

    @Query("SELECT ne FROM CaseNodeExtension ne WHERE ne.approvalStatus = 'PENDING' AND ne.isDeleted = false ORDER BY ne.applyTime")
    List<CaseNodeExtension> findPendingApprovals();

    @Query("SELECT ne FROM CaseNodeExtension ne WHERE ne.approverId = :approverId AND ne.approvalStatus = 'PENDING' AND ne.isDeleted = false ORDER BY ne.applyTime")
    List<CaseNodeExtension> findPendingByApproverId(@Param("approverId") Long approverId);

    @Query("SELECT ne FROM CaseNodeExtension ne WHERE ne.nodeInstanceId = :nodeInstanceId AND ne.approvalStatus = 'APPROVED' AND ne.isDeleted = false ORDER BY ne.approvalTime DESC")
    List<CaseNodeExtension> findApprovedByNodeInstanceId(@Param("nodeInstanceId") Long nodeInstanceId);

    @Query("SELECT ne FROM CaseNodeExtension ne WHERE ne.id = :id AND ne.isDeleted = false")
    Optional<CaseNodeExtension> findByIdAndNotDeleted(@Param("id") Long id);
}
