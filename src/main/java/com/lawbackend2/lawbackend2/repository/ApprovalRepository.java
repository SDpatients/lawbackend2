package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
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
    void deleteByCaseId(Long caseId);
}