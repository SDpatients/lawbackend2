package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FundApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundApprovalRepository extends JpaRepository<FundApproval, Long> {

    @Query("SELECT fa FROM FundApproval fa WHERE " +
           "(:caseId IS NULL OR fa.caseId = :caseId) " +
           "AND (:approvalStatus IS NULL OR fa.approvalStatus = :approvalStatus) " +
           "AND (:status IS NULL OR fa.status = :status)")
    Page<FundApproval> findByConditions(@Param("caseId") Long caseId,
                                        @Param("approvalStatus") String approvalStatus,
                                        @Param("status") String status,
                                        Pageable pageable);

    List<FundApproval> findByCaseId(Long caseId);

    @Query("SELECT fa FROM FundApproval fa WHERE fa.caseId = :caseId AND fa.createTime BETWEEN :startDate AND :endDate")
    List<FundApproval> findByCaseIdAndDateRange(@Param("caseId") Long caseId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT COUNT(fa) FROM FundApproval fa WHERE fa.caseId = :caseId AND fa.createTime BETWEEN :startDate AND :endDate")
    Long countByCaseIdAndDateRange(@Param("caseId") Long caseId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT SUM(fa.amount) FROM FundApproval fa WHERE fa.caseId = :caseId AND fa.createTime BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumAmountByCaseIdAndDateRange(@Param("caseId") Long caseId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT fa FROM FundApproval fa WHERE fa.caseId = :caseId")
    Page<FundApproval> findByCaseIdWithPage(@Param("caseId") Long caseId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM FundApproval fa WHERE fa.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
