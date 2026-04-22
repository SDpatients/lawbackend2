package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CommonDebt;
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
public interface CommonDebtRepository extends JpaRepository<CommonDebt, Long> {

    Optional<CommonDebt> findByDebtNo(String debtNo);

    @Query("SELECT cd FROM CommonDebt cd WHERE " +
           "(:caseId IS NULL OR cd.caseId = :caseId) " +
           "AND (:debtType IS NULL OR cd.debtType = :debtType) " +
           "AND (:approvalStatus IS NULL OR cd.approvalStatus = :approvalStatus) " +
           "AND (:repaymentStatus IS NULL OR cd.repaymentStatus = :repaymentStatus)")
    Page<CommonDebt> findByConditions(@Param("caseId") Long caseId,
                                      @Param("debtType") String debtType,
                                      @Param("approvalStatus") String approvalStatus,
                                      @Param("repaymentStatus") String repaymentStatus,
                                      Pageable pageable);

    List<CommonDebt> findByCaseId(Long caseId);

    @Modifying
    @Query("DELETE FROM CommonDebt cd WHERE cd.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}