package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ClaimReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimReviewRepository extends JpaRepository<ClaimReview, Long>, JpaSpecificationExecutor<ClaimReview> {
    Page<ClaimReview> findByClaimRegistrationId(Long claimRegistrationId, Pageable pageable);

    Page<ClaimReview> findByCaseId(Long caseId, Pageable pageable);

    Page<ClaimReview> findByReviewStatus(String reviewStatus, Pageable pageable);

    Page<ClaimReview> findByCaseIdAndReviewStatus(Long caseId, String reviewStatus, Pageable pageable);

    Page<ClaimReview> findByReviewConclusion(String reviewConclusion, Pageable pageable);

    Page<ClaimReview> findByClaimRegistrationIdAndReviewRound(Long claimRegistrationId, Integer reviewRound, Pageable pageable);

    Optional<ClaimReview> findFirstByClaimRegistrationIdOrderByReviewRoundDesc(Long claimRegistrationId);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.claimRegistrationId = :claimRegistrationId")
    Long countByClaimRegistrationId(@Param("claimRegistrationId") Long claimRegistrationId);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.reviewStatus = :reviewStatus")
    Long countByReviewStatus(@Param("reviewStatus") String reviewStatus);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus = :reviewStatus")
    Long countByCaseIdAndReviewStatus(@Param("caseId") Long caseId, @Param("reviewStatus") String reviewStatus);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.reviewConclusion = :reviewConclusion")
    Long countByReviewConclusion(@Param("reviewConclusion") String reviewConclusion);

    @Query("SELECT r.reviewConclusion, COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId GROUP BY r.reviewConclusion")
    List<Object[]> countByReviewConclusionGroupByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT r.reviewStatus, COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId GROUP BY r.reviewStatus")
    List<Object[]> countByReviewStatusGroupByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT r FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus = 'PENDING'")
    List<ClaimReview> findPendingReviewsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT r FROM ClaimReview r WHERE r.claimRegistrationId = :claimRegistrationId ORDER BY r.reviewRound DESC")
    List<ClaimReview> findAllByClaimRegistrationIdOrderByReviewRoundDesc(@Param("claimRegistrationId") Long claimRegistrationId);

    @Query("SELECT r FROM ClaimReview r WHERE r.claimRegistrationId = :claimRegistrationId")
    List<ClaimReview> findAllByClaimRegistrationId(@Param("claimRegistrationId") Long claimRegistrationId);

    @Query("SELECT r FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus = :reviewStatus")
    List<ClaimReview> findByCaseIdAndReviewStatus(@Param("caseId") Long caseId, @Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.reviewStatus = :reviewStatus")
    List<ClaimReview> findByReviewStatus(@Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus != :reviewStatus")
    List<ClaimReview> findByCaseIdAndReviewStatusNot(@Param("caseId") Long caseId, @Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.reviewStatus != :reviewStatus")
    List<ClaimReview> findByReviewStatusNot(@Param("reviewStatus") String reviewStatus);

    @Modifying
    @Query("DELETE FROM ClaimReview r WHERE r.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    List<ClaimReview> findAllByCreditorName(String creditorName);
}
