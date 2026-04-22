package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ExpenseReimbursement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseReimbursementRepository extends JpaRepository<ExpenseReimbursement, Long>, JpaSpecificationExecutor<ExpenseReimbursement> {

    ExpenseReimbursement findByReimbursementNumber(String reimbursementNumber);

    Page<ExpenseReimbursement> findByCaseId(Long caseId, Pageable pageable);

    Page<ExpenseReimbursement> findByApplicantId(Long applicantId, Pageable pageable);

    Page<ExpenseReimbursement> findByApprovalStatus(String approvalStatus, Pageable pageable);

    Page<ExpenseReimbursement> findByReimbursementDate(LocalDate reimbursementDate, Pageable pageable);

    Page<ExpenseReimbursement> findByCaseIdAndApprovalStatus(Long caseId, String approvalStatus, Pageable pageable);

    Page<ExpenseReimbursement> findByApplicantIdAndApprovalStatus(Long applicantId, String approvalStatus, Pageable pageable);

    @Query("SELECT COUNT(e) FROM ExpenseReimbursement e WHERE e.reimbursementNumber LIKE :prefix%")
    Long countByReimbursementNumberPrefix(@Param("prefix") String prefix);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE (:caseId IS NULL OR e.caseId = :caseId) " +
            "AND (:applicantId IS NULL OR e.applicantId = :applicantId) " +
            "AND (:approvalStatus IS NULL OR e.approvalStatus = :approvalStatus) " +
            "AND (:reimbursementDate IS NULL OR e.reimbursementDate = :reimbursementDate) " +
            "ORDER BY e.createTime DESC")
    Page<ExpenseReimbursement> findByConditions(@Param("caseId") Long caseId,
                                                @Param("applicantId") Long applicantId,
                                                @Param("approvalStatus") String approvalStatus,
                                                @Param("reimbursementDate") LocalDate reimbursementDate,
                                                Pageable pageable);

    List<ExpenseReimbursement> findByFundAccountId(Long fundAccountId);

    @Modifying
    @Query("DELETE FROM ExpenseReimbursement e WHERE e.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
