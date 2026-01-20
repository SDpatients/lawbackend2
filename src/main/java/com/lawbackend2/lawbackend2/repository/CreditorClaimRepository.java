package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CreditorClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditorClaimRepository extends JpaRepository<CreditorClaim, Long>, JpaSpecificationExecutor<CreditorClaim> {
    Page<CreditorClaim> findByCaseId(Long caseId, Pageable pageable);

    Page<CreditorClaim> findByRegistrationStatus(String registrationStatus, Pageable pageable);

    Page<CreditorClaim> findByCaseIdAndRegistrationStatus(Long caseId, String registrationStatus, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CreditorClaim c")
    Long countTotalClaims();

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.registrationStatus = :status")
    Long countByRegistrationStatus(@Param("status") String status);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.caseId = :caseId")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT SUM(c.principal) FROM CreditorClaim c")
    Optional<java.math.BigDecimal> sumPrincipal();

    @Query("SELECT SUM(c.interest) FROM CreditorClaim c")
    Optional<java.math.BigDecimal> sumInterest();

    @Query("SELECT SUM(c.penalty) FROM CreditorClaim c")
    Optional<java.math.BigDecimal> sumPenalty();

    @Query("SELECT SUM(c.otherLosses) FROM CreditorClaim c")
    Optional<java.math.BigDecimal> sumOtherLosses();

    @Query("SELECT SUM(c.totalAmount) FROM CreditorClaim c")
    Optional<java.math.BigDecimal> sumTotalAmount();

    @Query("SELECT AVG(c.totalAmount) FROM CreditorClaim c")
    Optional<java.lang.Double> getAverageClaimAmount();

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE DATE(c.createTime) = :date")
    Long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE YEAR(c.createTime) = :year AND MONTH(c.createTime) = :month")
    Long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE YEAR(c.createTime) = :year")
    Long countByCreatedAtYear(@Param("year") int year);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM CreditorClaim c GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroup();

    @Query("SELECT c.claimType, COUNT(c) FROM CreditorClaim c WHERE c.claimType IS NOT NULL GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroup();

    @Query("SELECT c.claimNature, COUNT(c) FROM CreditorClaim c WHERE c.claimNature IS NOT NULL GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroup();

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.hasCourtJudgment = :hasCourtJudgment")
    Long countByHasCourtJudgment(@Param("hasCourtJudgment") Boolean hasCourtJudgment);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.hasExecution = :hasExecution")
    Long countByHasExecution(@Param("hasExecution") Boolean hasExecution);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.hasCollateral = :hasCollateral")
    Long countByHasCollateral(@Param("hasCollateral") Boolean hasCollateral);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.createTime BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM CreditorClaim c WHERE c.createTime BETWEEN :startDate AND :endDate GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimType, COUNT(c) FROM CreditorClaim c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.claimType IS NOT NULL GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimNature, COUNT(c) FROM CreditorClaim c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.claimNature IS NOT NULL GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.caseId, SUM(c.totalAmount) FROM CreditorClaim c WHERE c.isDeleted = false GROUP BY c.caseId ORDER BY SUM(c.totalAmount) DESC")
    List<Object[]> sumTotalAmountByCaseIdGroup();

    @Query("SELECT c.caseId, c.caseName, SUM(c.totalAmount) FROM CreditorClaim c WHERE c.isDeleted = false GROUP BY c.caseId, c.caseName ORDER BY SUM(c.totalAmount) DESC")
    List<Object[]> sumTotalAmountByCaseIdWithNameGroup();

    @Query("SELECT c.id, c.creditorName, c.totalAmount FROM CreditorClaim c WHERE c.isDeleted = false ORDER BY c.totalAmount DESC")
    List<Object[]> findTopClaimsByAmount();

    @Query("SELECT c.id, c.creditorName, c.totalAmount FROM CreditorClaim c WHERE c.isDeleted = false ORDER BY c.totalAmount DESC")
    List<Object[]> findTopClaimsByAmount(org.springframework.data.domain.Pageable pageable);
}
