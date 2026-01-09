package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.BankruptCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BankruptCaseRepository extends JpaRepository<BankruptCase, Long>, JpaSpecificationExecutor<BankruptCase> {
    Optional<BankruptCase> findByCaseNumber(String caseNumber);

    Page<BankruptCase> findByCaseStatus(String caseStatus, Pageable pageable);

    Page<BankruptCase> findByCaseProgress(String caseProgress, Pageable pageable);

    Page<BankruptCase> findByCaseStatusAndCaseProgress(String caseStatus, String caseProgress, Pageable pageable);

    @Query("SELECT COUNT(c) FROM BankruptCase c")
    Long countTotalCases();

    @Query("SELECT COUNT(c) FROM BankruptCase c WHERE c.caseStatus = :status")
    Long countByCaseStatus(@Param("status") String status);

    @Query("SELECT COUNT(c) FROM BankruptCase c WHERE c.caseProgress = :progress")
    Long countByCaseProgress(@Param("progress") String progress);

    @Query("SELECT COUNT(c) FROM BankruptCase c WHERE c.isSimplifiedTrial = :isSimplified")
    Long countByIsSimplifiedTrial(@Param("isSimplified") Boolean isSimplified);

    @Query("SELECT AVG(c.reviewCount) FROM BankruptCase c")
    Double getAverageReviewCount();

    @Query("SELECT COUNT(c) FROM BankruptCase c WHERE DATE(c.createTime) = :date")
    Long countByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(c) FROM BankruptCase c WHERE YEAR(c.createTime) = :year AND MONTH(c.createTime) = :month")
    Long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT COUNT(c) FROM BankruptCase c WHERE YEAR(c.createTime) = :year")
    Long countByCreatedAtYear(@Param("year") int year);

    @Query("SELECT c.caseStatus, COUNT(c) FROM BankruptCase c GROUP BY c.caseStatus")
    List<Object[]> countByCaseStatusGroup();

    @Query("SELECT c.caseProgress, COUNT(c) FROM BankruptCase c GROUP BY c.caseProgress")
    List<Object[]> countByCaseProgressGroup();

    @Query("SELECT COUNT(c) FROM BankruptCase c WHERE c.createTime BETWEEN :startDate AND :endDate")
    Long countByCreatedAtBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.caseStatus, COUNT(c) FROM BankruptCase c WHERE c.createTime BETWEEN :startDate AND :endDate GROUP BY c.caseStatus")
    List<Object[]> countByCaseStatusGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c.caseProgress, COUNT(c) FROM BankruptCase c WHERE c.createTime BETWEEN :startDate AND :endDate GROUP BY c.caseProgress")
    List<Object[]> countByCaseProgressGroupByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseNumber LIKE %:keyword%")
    Page<BankruptCase> searchByCaseNumber(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseName LIKE %:keyword%")
    Page<BankruptCase> searchByCaseName(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.acceptanceCourt LIKE %:keyword%")
    Page<BankruptCase> searchByAcceptanceCourt(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.designatedInstitution LIKE %:keyword%")
    Page<BankruptCase> searchByDesignatedInstitution(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.mainResponsiblePerson LIKE %:keyword%")
    Page<BankruptCase> searchByMainResponsiblePerson(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.acceptanceDate BETWEEN :startDate AND :endDate")
    Page<BankruptCase> searchByAcceptanceDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseSource LIKE %:keyword%")
    Page<BankruptCase> searchByCaseSource(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseReason LIKE %:keyword%")
    Page<BankruptCase> searchByCaseReason(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.designatedJudge LIKE %:keyword%")
    Page<BankruptCase> searchByDesignatedJudge(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseNumber LIKE %:keyword% OR c.caseName LIKE %:keyword% OR c.acceptanceCourt LIKE %:keyword% OR c.designatedInstitution LIKE %:keyword% OR c.mainResponsiblePerson LIKE %:keyword% OR c.caseSource LIKE %:keyword% OR c.caseReason LIKE %:keyword% OR c.designatedJudge LIKE %:keyword%")
    Page<BankruptCase> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseNumber LIKE %:keyword% OR c.caseName LIKE %:keyword% AND c.caseStatus = :caseStatus")
    Page<BankruptCase> searchByKeywordAndCaseStatus(@Param("keyword") String keyword, @Param("caseStatus") String caseStatus, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseNumber LIKE %:keyword% OR c.caseName LIKE %:keyword% AND c.caseProgress = :caseProgress")
    Page<BankruptCase> searchByKeywordAndCaseProgress(@Param("keyword") String keyword, @Param("caseProgress") String caseProgress, Pageable pageable);

    @Query("SELECT c FROM BankruptCase c WHERE c.caseNumber LIKE %:keyword% OR c.caseName LIKE %:keyword% AND c.caseStatus = :caseStatus AND c.caseProgress = :caseProgress")
    Page<BankruptCase> searchByKeywordAndStatusAndProgress(@Param("keyword") String keyword, @Param("caseStatus") String caseStatus, @Param("caseProgress") String caseProgress, Pageable pageable);

    @Query("SELECT new com.lawbackend2.lawbackend2.dto.CaseSimpleInfo(c.id, c.caseNumber, c.caseName) FROM BankruptCase c WHERE (:caseNumber IS NULL OR :caseNumber = '' OR c.caseNumber LIKE %:caseNumber%)")
    Page<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> findSimpleInfoByCaseNumber(@Param("caseNumber") String caseNumber, Pageable pageable);
}
