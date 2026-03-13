package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.CreditorClaim;
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
import java.util.Optional;

@Repository
public interface CreditorClaimRepository extends JpaRepository<CreditorClaim, Long>, JpaSpecificationExecutor<CreditorClaim> {
    @Query("SELECT c FROM CreditorClaim c WHERE c.isDeleted = false AND c.caseId = :caseId")
    Page<CreditorClaim> findByCaseId(@Param("caseId") Long caseId, Pageable pageable);

    @Query("SELECT c FROM CreditorClaim c WHERE c.isDeleted = false AND c.registrationStatus = :registrationStatus")
    Page<CreditorClaim> findByRegistrationStatus(@Param("registrationStatus") String registrationStatus, Pageable pageable);

    @Query("SELECT c FROM CreditorClaim c WHERE c.isDeleted = false AND c.caseId = :caseId AND c.registrationStatus = :registrationStatus")
    Page<CreditorClaim> findByCaseIdAndRegistrationStatus(@Param("caseId") Long caseId, @Param("registrationStatus") String registrationStatus, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false")
    Long countTotalClaims();

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.registrationStatus = :status")
    Long countByRegistrationStatus(@Param("status") String status);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.caseId = :caseId")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT SUM(c.principal) FROM CreditorClaim c WHERE c.isDeleted = false")
    Optional<java.math.BigDecimal> sumPrincipal();

    @Query("SELECT SUM(c.interest) FROM CreditorClaim c WHERE c.isDeleted = false")
    Optional<java.math.BigDecimal> sumInterest();

    @Query("SELECT SUM(c.penalty) FROM CreditorClaim c WHERE c.isDeleted = false")
    Optional<java.math.BigDecimal> sumPenalty();

    @Query("SELECT SUM(c.otherLosses) FROM CreditorClaim c WHERE c.isDeleted = false")
    Optional<java.math.BigDecimal> sumOtherLosses();

    @Query("SELECT SUM(c.totalAmount) FROM CreditorClaim c WHERE c.isDeleted = false")
    Optional<java.math.BigDecimal> sumTotalAmount();

    @Query("SELECT AVG(c.totalAmount) FROM CreditorClaim c WHERE c.isDeleted = false")
    Optional<java.lang.Double> getAverageClaimAmount();

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND DATE(c.createTime) = :date")
    Long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND YEAR(c.createTime) = :year AND MONTH(c.createTime) = :month")
    Long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND YEAR(c.createTime) = :year")
    Long countByCreatedAtYear(@Param("year") int year);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroup();

    @Query("SELECT c.claimType, COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.claimType IS NOT NULL GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroup();

    @Query("SELECT c.claimNature, COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.claimNature IS NOT NULL GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroup();

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.hasCourtJudgment = :hasCourtJudgment")
    Long countByHasCourtJudgment(@Param("hasCourtJudgment") Boolean hasCourtJudgment);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.hasExecution = :hasExecution")
    Long countByHasExecution(@Param("hasExecution") Boolean hasExecution);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.hasCollateral = :hasCollateral")
    Long countByHasCollateral(@Param("hasCollateral") Boolean hasCollateral);

    @Query("SELECT COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.createTime BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.createTime BETWEEN :startDate AND :endDate GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimType, COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.createTime BETWEEN :startDate AND :endDate AND c.claimType IS NOT NULL GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimNature, COUNT(c) FROM CreditorClaim c WHERE c.isDeleted = false AND c.createTime BETWEEN :startDate AND :endDate AND c.claimNature IS NOT NULL GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.caseId, SUM(c.totalAmount) FROM CreditorClaim c WHERE c.isDeleted = false GROUP BY c.caseId ORDER BY SUM(c.totalAmount) DESC")
    List<Object[]> sumTotalAmountByCaseIdGroup();

    @Query("SELECT c.caseId, c.caseName, SUM(c.totalAmount) FROM CreditorClaim c WHERE c.isDeleted = false GROUP BY c.caseId, c.caseName ORDER BY SUM(c.totalAmount) DESC")
    List<Object[]> sumTotalAmountByCaseIdWithNameGroup();

    @Query("SELECT c.id, c.creditorName, c.totalAmount FROM CreditorClaim c WHERE c.isDeleted = false ORDER BY c.totalAmount DESC")
    List<Object[]> findTopClaimsByAmount();

    @Query("SELECT c.id, c.creditorName, c.totalAmount FROM CreditorClaim c WHERE c.isDeleted = false ORDER BY c.totalAmount DESC")
    List<Object[]> findTopClaimsByAmount(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT c.caseId, c.caseName, SUM(c.totalAmount) FROM CreditorClaim c WHERE c.isDeleted = false AND c.caseId IN (SELECT bc.id FROM BankruptCase bc WHERE bc.createUserId = :userId) GROUP BY c.caseId, c.caseName ORDER BY SUM(c.totalAmount) DESC")
    List<Object[]> sumTotalAmountByCaseIdWithNameGroupByUserId(@Param("userId") Long userId);

    @Query("SELECT c.id, c.creditorName, c.totalAmount FROM CreditorClaim c WHERE c.isDeleted = false AND c.caseId IN (SELECT bc.id FROM BankruptCase bc WHERE bc.createUserId = :userId) ORDER BY c.totalAmount DESC")
    List<Object[]> findTopClaimsByAmountByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Query("UPDATE CreditorClaim c SET c.isDeleted = true WHERE c.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);
}
