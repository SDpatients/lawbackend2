package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FundApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundApprovalRepository extends JpaRepository<FundApproval, Long> {

    @Query("SELECT fa FROM FundApproval fa WHERE fa.isDeleted = false " +
           "AND (:caseId IS NULL OR fa.caseId = :caseId) " +
           "AND (:approvalStatus IS NULL OR fa.approvalStatus = :approvalStatus) " +
           "AND (:status IS NULL OR fa.status = :status)")
    Page<FundApproval> findByConditions(@Param("caseId") Long caseId,
                                        @Param("approvalStatus") String approvalStatus,
                                        @Param("status") String status,
                                        Pageable pageable);

    List<FundApproval> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);
}
