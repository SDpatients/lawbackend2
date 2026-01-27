package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.FundBudget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundBudgetRepository extends JpaRepository<FundBudget, Long> {

    Optional<FundBudget> findByBudgetNoAndIsDeleted(String budgetNo, Boolean isDeleted);

    @Query("SELECT fb FROM FundBudget fb WHERE fb.isDeleted = false " +
           "AND (:caseId IS NULL OR fb.caseId = :caseId) " +
           "AND (:budgetType IS NULL OR fb.budgetType = :budgetType) " +
           "AND (:budgetStatus IS NULL OR fb.budgetStatus = :budgetStatus) " +
           "AND (:approvalStatus IS NULL OR fb.approvalStatus = :approvalStatus)")
    Page<FundBudget> findByConditions(@Param("caseId") Long caseId,
                                      @Param("budgetType") String budgetType,
                                      @Param("budgetStatus") String budgetStatus,
                                      @Param("approvalStatus") String approvalStatus,
                                      Pageable pageable);

    List<FundBudget> findByCaseIdAndIsDeleted(Long caseId, Boolean isDeleted);
    void deleteByCaseId(Long caseId);
}