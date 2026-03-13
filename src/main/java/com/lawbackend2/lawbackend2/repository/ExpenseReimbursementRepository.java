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

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.reimbursementNumber = :reimbursementNumber")
    ExpenseReimbursement findByReimbursementNumber(@Param("reimbursementNumber") String reimbursementNumber);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.caseId = :caseId")
    Page<ExpenseReimbursement> findByCaseId(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.applicantId = :applicantId")
    Page<ExpenseReimbursement> findByApplicantId(@Param("applicantId") Long applicantId, Pageable pageable);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.approvalStatus = :approvalStatus")
    Page<ExpenseReimbursement> findByApprovalStatus(@Param("approvalStatus") String approvalStatus, Pageable pageable);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.reimbursementDate = :reimbursementDate")
    Page<ExpenseReimbursement> findByReimbursementDate(@Param("reimbursementDate") LocalDate reimbursementDate, Pageable pageable);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.caseId = :caseId AND e.approvalStatus = :approvalStatus")
    Page<ExpenseReimbursement> findByCaseIdAndApprovalStatus(@Param("caseId") Long caseId, @Param("approvalStatus") String approvalStatus, Pageable pageable);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.applicantId = :applicantId AND e.approvalStatus = :approvalStatus")
    Page<ExpenseReimbursement> findByApplicantIdAndApprovalStatus(@Param("applicantId") Long applicantId, @Param("approvalStatus") String approvalStatus, Pageable pageable);

    @Query("SELECT COUNT(e) FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.reimbursementNumber LIKE :prefix%")
    Long countByReimbursementNumberPrefix(@Param("prefix") String prefix);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND (:caseId IS NULL OR e.caseId = :caseId) " +
            "AND (:applicantId IS NULL OR e.applicantId = :applicantId) " +
            "AND (:approvalStatus IS NULL OR e.approvalStatus = :approvalStatus) " +
            "AND (:reimbursementDate IS NULL OR e.reimbursementDate = :reimbursementDate) " +
            "ORDER BY e.createTime DESC")
    Page<ExpenseReimbursement> findByConditions(@Param("caseId") Long caseId,
                                                @Param("applicantId") Long applicantId,
                                                @Param("approvalStatus") String approvalStatus,
                                                @Param("reimbursementDate") LocalDate reimbursementDate,
                                                Pageable pageable);

    @Query("SELECT e FROM ExpenseReimbursement e WHERE e.isDeleted = false AND e.fundAccountId = :fundAccountId")
    List<ExpenseReimbursement> findByFundAccountId(@Param("fundAccountId") Long fundAccountId);

    @Modifying
    @Query("UPDATE ExpenseReimbursement e SET e.isDeleted = true WHERE e.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
