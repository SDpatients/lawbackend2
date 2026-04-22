package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.BankruptcyExpense;
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
public interface BankruptcyExpenseRepository extends JpaRepository<BankruptcyExpense, Long> {

    Optional<BankruptcyExpense> findByExpenseNo(String expenseNo);

    @Query("SELECT be FROM BankruptcyExpense be WHERE " +
           "(:caseId IS NULL OR be.caseId = :caseId) " +
           "AND (:expenseType IS NULL OR be.expenseType = :expenseType) " +
           "AND (:approvalStatus IS NULL OR be.approvalStatus = :approvalStatus) " +
           "AND (:paymentStatus IS NULL OR be.paymentStatus = :paymentStatus)")
    Page<BankruptcyExpense> findByConditions(@Param("caseId") Long caseId,
                                            @Param("expenseType") String expenseType,
                                            @Param("approvalStatus") String approvalStatus,
                                            @Param("paymentStatus") String paymentStatus,
                                            Pageable pageable);

    List<BankruptcyExpense> findByCaseId(Long caseId);

    @Modifying
    @Query("DELETE FROM BankruptcyExpense be WHERE be.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}