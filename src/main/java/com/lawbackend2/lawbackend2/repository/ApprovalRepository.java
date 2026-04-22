package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByCaseId(Long caseId);

    List<Approval> findByLawyerId(Long lawyerId);

    List<Approval> findByApprovalStatus(String approvalStatus);

    List<Approval> findByApproverId(Long approverId);

    boolean existsByCaseIdAndApprovalType(Long caseId, String approvalType);

    Optional<Approval> findByCaseIdAndApprovalType(Long caseId, String approvalType);

    @Modifying
    @Query("DELETE FROM Approval a WHERE a.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    long countByApprovalResultIsNullAndApprovalType(String approvalType);

    List<Approval> findByApprovalResultIsNullAndApprovalType(String approvalType);

    List<Approval> findByApprovalResultIsNull();

    List<Approval> findAll();
}
