package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ApprovalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByApprovalId(Long approvalId);
    Page<ApprovalHistory> findByApprovalId(Long approvalId, Pageable pageable);
    List<ApprovalHistory> findByCaseId(Long caseId);
    Page<ApprovalHistory> findByCaseId(Long caseId, Pageable pageable);
    List<ApprovalHistory> findByApproverId(Long approverId);
    Page<ApprovalHistory> findByApproverId(Long approverId, Pageable pageable);
    void deleteByCaseId(Long caseId);
}