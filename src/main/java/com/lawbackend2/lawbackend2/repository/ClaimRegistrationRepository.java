package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRegistrationRepository extends JpaRepository<ClaimRegistration, Long>, JpaSpecificationExecutor<ClaimRegistration> {
    Optional<ClaimRegistration> findByClaimNoAndIsDeletedFalse(String claimNo);

    Page<ClaimRegistration> findByCaseIdAndIsDeletedFalse(Long caseId, Pageable pageable);

    Page<ClaimRegistration> findByRegistrationStatusAndIsDeletedFalse(String registrationStatus, Pageable pageable);

    Page<ClaimRegistration> findByCaseIdAndRegistrationStatusAndIsDeletedFalse(Long caseId, String registrationStatus, Pageable pageable);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.caseId = :caseId AND c.isDeleted = false")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.registrationStatus = :status AND c.isDeleted = false")
    Long countByRegistrationStatus(@Param("status") String status);

    @Query("SELECT SUM(c.principal) FROM ClaimRegistration c WHERE c.isDeleted = false")
    Optional<BigDecimal> sumPrincipal();

    @Query("SELECT SUM(c.interest) FROM ClaimRegistration c WHERE c.isDeleted = false")
    Optional<BigDecimal> sumInterest();

    @Query("SELECT SUM(c.penalty) FROM ClaimRegistration c WHERE c.isDeleted = false")
    Optional<BigDecimal> sumPenalty();

    @Query("SELECT SUM(c.otherLosses) FROM ClaimRegistration c WHERE c.isDeleted = false")
    Optional<BigDecimal> sumOtherLosses();

    @Query("SELECT SUM(c.totalAmount) FROM ClaimRegistration c WHERE c.isDeleted = false")
    Optional<BigDecimal> sumTotalAmount();

    @Query("SELECT AVG(c.totalAmount) FROM ClaimRegistration c WHERE c.isDeleted = false")
    Optional<Double> getAverageClaimAmount();

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE DATE(c.createTime) = :date AND c.isDeleted = false")
    Long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE YEAR(c.createTime) = :year AND MONTH(c.createTime) = :month AND c.isDeleted = false")
    Long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE YEAR(c.createTime) = :year AND c.isDeleted = false")
    Long countByCreatedAtYear(@Param("year") int year);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM ClaimRegistration c WHERE c.isDeleted = false GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroup();

    @Query("SELECT c.claimType, COUNT(c) FROM ClaimRegistration c WHERE c.claimType IS NOT NULL AND c.isDeleted = false GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroup();

    @Query("SELECT c.claimNature, COUNT(c) FROM ClaimRegistration c WHERE c.claimNature IS NOT NULL AND c.isDeleted = false GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroup();

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.hasCourtJudgment = :hasCourtJudgment AND c.isDeleted = false")
    Long countByHasCourtJudgment(@Param("hasCourtJudgment") Boolean hasCourtJudgment);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.hasExecution = :hasExecution AND c.isDeleted = false")
    Long countByHasExecution(@Param("hasExecution") Boolean hasExecution);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.hasCollateral = :hasCollateral AND c.isDeleted = false")
    Long countByHasCollateral(@Param("hasCollateral") Boolean hasCollateral);

    @Query("SELECT COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.isDeleted = false")
    Long countByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.registrationStatus, COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.isDeleted = false GROUP BY c.registrationStatus")
    List<Object[]> countByRegistrationStatusGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimType, COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.claimType IS NOT NULL AND c.isDeleted = false GROUP BY c.claimType")
    List<Object[]> countByClaimTypeGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.claimNature, COUNT(c) FROM ClaimRegistration c WHERE c.createTime BETWEEN :startDate AND :endDate AND c.claimNature IS NOT NULL AND c.isDeleted = false GROUP BY c.claimNature")
    List<Object[]> countByClaimNatureGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Modifying
    @Query("UPDATE ClaimRegistration c SET c.isDeleted = true WHERE c.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    List<ClaimRegistration> findAllByCreditorNameAndIsDeletedFalse(String creditorName);

    @Query("SELECT MAX(c.claimNo) FROM ClaimRegistration c WHERE c.claimNo LIKE :prefix% AND c.isDeleted = false")
    Optional<String> findMaxClaimNoByPrefix(@Param("prefix") String prefix);
}
