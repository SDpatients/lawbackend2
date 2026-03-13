package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FundReimbursement;
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
public interface FundReimbursementRepository extends JpaRepository<FundReimbursement, Long> {

    Optional<FundReimbursement> findByReimbursementNoAndIsDeleted(String reimbursementNo, Boolean isDeleted);

    @Query("SELECT fr FROM FundReimbursement fr WHERE fr.isDeleted = false " +
           "AND (:caseId IS NULL OR fr.caseId = :caseId) " +
           "AND (:reimbursementType IS NULL OR fr.reimbursementType = :reimbursementType) " +
           "AND (:applicantId IS NULL OR fr.applicantId = :applicantId) " +
           "AND (:approvalStatus IS NULL OR fr.approvalStatus = :approvalStatus) " +
           "AND (:paymentStatus IS NULL OR fr.paymentStatus = :paymentStatus)")
    Page<FundReimbursement> findByConditions(@Param("caseId") Long caseId,
                                              @Param("reimbursementType") String reimbursementType,
                                              @Param("applicantId") Long applicantId,
                                              @Param("approvalStatus") String approvalStatus,
                                              @Param("paymentStatus") String paymentStatus,
                                              Pageable pageable);

    List<FundReimbursement> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);

    @Modifying
    @Query("UPDATE FundReimbursement fr SET fr.isDeleted = true WHERE fr.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}