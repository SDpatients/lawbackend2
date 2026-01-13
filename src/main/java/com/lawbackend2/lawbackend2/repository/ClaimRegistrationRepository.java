package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRegistrationRepository extends JpaRepository<ClaimRegistration, Long>, JpaSpecificationExecutor<ClaimRegistration> {
    Optional<ClaimRegistration> findByClaimNo(String claimNo);

    Page<ClaimRegistration> findByCaseId(Long caseId, Pageable pageable);

    Page<ClaimRegistration> findByRegistrationStatus(String registrationStatus, Pageable pageable);

    Page<ClaimRegistration> findByCaseIdAndRegistrationStatus(Long caseId, String registrationStatus, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.caseId = :caseId")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.registrationStatus = :status")
    Long countByRegistrationStatus(@Param("status") String status);

    @Query("SELECT SUM(c.principal) FROM ClaimRegistration c")
    Optional<BigDecimal> sumPrincipal();

    @Query("SELECT SUM(c.interest) FROM ClaimRegistration c")
    Optional<BigDecimal> sumInterest();

    @Query("SELECT SUM(c.penalty) FROM ClaimRegistration c")
    Optional<BigDecimal> sumPenalty();

    @Query("SELECT SUM(c.otherLosses) FROM ClaimRegistration c")
    Optional<BigDecimal> sumOtherLosses();

    @Query("SELECT SUM(c.totalAmount) FROM ClaimRegistration c")
    Optional<BigDecimal> sumTotalAmount();

    @Query("SELECT AVG(c.totalAmount) FROM ClaimRegistration c")
    Optional<Double> getAverageClaimAmount();

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE DATE(c.createTime) = :date")
    Long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE YEAR(c.createTime) = :year AND MONTH(c.createTime) = :month")
    Long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE YEAR(c.createTime) = :year")
    Long countByCreatedAtYear(@Param("year") int year);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM ClaimRegistration c GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroup();

    @Query("SELECT c.claimType, COUNT(c) FROM ClaimRegistration c WHERE c.claimType IS NOT NULL GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroup();

    @Query("SELECT c.claimNature, COUNT(c) FROM ClaimRegistration c WHERE c.claimNature IS NOT NULL GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroup();

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.hasCourtJudgment = :hasCourtJudgment")
    Long countByHasCourtJudgment(@Param("hasCourtJudgment") Boolean hasCourtJudgment);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.hasExecution = :hasExecution")
    Long countByHasExecution(@Param("hasExecution") Boolean hasExecution);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.hasCollateral = :hasCollateral")
    Long countByHasCollateral(@Param("hasCollateral") Boolean hasCollateral);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimType, COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.claimType IS NOT NULL GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimNature, COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.claimNature IS NOT NULL GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
