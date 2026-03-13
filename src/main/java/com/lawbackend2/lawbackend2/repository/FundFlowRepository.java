package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FundFlow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FundFlowRepository extends JpaRepository<FundFlow, Long> {

    @Query("SELECT ff FROM FundFlow ff WHERE ff.isDeleted = false " +
           "AND (:caseId IS NULL OR ff.caseId = :caseId) " +
           "AND (:fundAccountId IS NULL OR ff.fundAccountId = :fundAccountId) " +
           "AND (:flowType IS NULL OR ff.flowType = :flowType) " +
           "AND (:status IS NULL OR ff.status = :status)")
    Page<FundFlow> findByConditions(@Param("caseId") Long caseId,
                                     @Param("fundAccountId") Long fundAccountId,
                                     @Param("flowType") String flowType,
                                     @Param("status") String status,
                                     Pageable pageable);

    List<FundFlow> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);

    @Query("SELECT ff FROM FundFlow ff WHERE ff.isDeleted = false AND ff.caseId = :caseId AND ff.transactionDate BETWEEN :startDate AND :endDate")
    List<FundFlow> findByCaseIdAndDateRange(@Param("caseId") Long caseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(ff) FROM FundFlow ff WHERE ff.isDeleted = false AND ff.caseId = :caseId AND ff.transactionDate BETWEEN :startDate AND :endDate")
    Long countByCaseIdAndDateRange(@Param("caseId") Long caseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(ff.amount) FROM FundFlow ff WHERE ff.isDeleted = false AND ff.caseId = :caseId AND ff.transactionDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumAmountByCaseIdAndDateRange(@Param("caseId") Long caseId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Query("UPDATE FundFlow ff SET ff.isDeleted = true WHERE ff.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
