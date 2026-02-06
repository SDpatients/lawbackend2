package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.ClaimReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimReviewRepository extends JpaRepository<ClaimReview, Long>, JpaSpecificationExecutor<ClaimReview> {
    Page<ClaimReview> findByClaimRegistrationIdAndIsDeletedFalse(Long claimRegistrationId, Pageable pageable);

    Page<ClaimReview> findByCaseIdAndIsDeletedFalse(Long caseId, Pageable pageable);

    Page<ClaimReview> findByReviewStatusAndIsDeletedFalse(String reviewStatus, Pageable pageable);

    Page<ClaimReview> findByCaseIdAndReviewStatusAndIsDeletedFalse(Long caseId, String reviewStatus, Pageable pageable);

    Page<ClaimReview> findByReviewConclusionAndIsDeletedFalse(String reviewConclusion, Pageable pageable);

    Page<ClaimReview> findByClaimRegistrationIdAndReviewRoundAndIsDeletedFalse(Long claimRegistrationId, Integer reviewRound, Pageable pageable);

    Optional<ClaimReview> findFirstByClaimRegistrationIdAndIsDeletedFalseOrderByReviewRoundDesc(Long claimRegistrationId);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.claimRegistrationId = :claimRegistrationId AND r.isDeleted = false")
    Long countByClaimRegistrationId(@Param("claimRegistrationId") Long claimRegistrationId);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId AND r.isDeleted = false")
    Long countByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.reviewStatus = :reviewStatus AND r.isDeleted = false")
    Long countByReviewStatus(@Param("reviewStatus") String reviewStatus);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus = :reviewStatus AND r.isDeleted = false")
    Long countByCaseIdAndReviewStatus(@Param("caseId") Long caseId, @Param("reviewStatus") String reviewStatus);

    @Query("SELECT COUNT(r) FROM ClaimReview r WHERE r.reviewConclusion = :reviewConclusion AND r.isDeleted = false")
    Long countByReviewConclusion(@Param("reviewConclusion") String reviewConclusion);

    @Query("SELECT r.reviewConclusion, COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId AND r.isDeleted = false GROUP BY r.reviewConclusion")
    List<Object[]> countByReviewConclusionGroupByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT r.reviewStatus, COUNT(r) FROM ClaimReview r WHERE r.caseId = :caseId AND r.isDeleted = false GROUP BY r.reviewStatus")
    List<Object[]> countByReviewStatusGroupByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT r FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus = 'PENDING' AND r.isDeleted = false")
    List<ClaimReview> findPendingReviewsByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT r FROM ClaimReview r WHERE r.claimRegistrationId = :claimRegistrationId AND r.isDeleted = false ORDER BY r.reviewRound DESC")
    List<ClaimReview> findAllByClaimRegistrationIdOrderByReviewRoundDesc(@Param("claimRegistrationId") Long claimRegistrationId);

    @Query("SELECT r FROM ClaimReview r WHERE r.claimRegistrationId = :claimRegistrationId AND r.isDeleted = false")
    List<ClaimReview> findAllByClaimRegistrationIdAndIsDeletedFalse(@Param("claimRegistrationId") Long claimRegistrationId);

    @Query("SELECT r FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus = :reviewStatus AND r.isDeleted = false")
    List<ClaimReview> findByCaseIdAndReviewStatusAndIsDeletedFalse(@Param("caseId") Long caseId, @Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.reviewStatus = :reviewStatus AND r.isDeleted = false")
    List<ClaimReview> findByReviewStatusAndIsDeletedFalse(@Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus != :reviewStatus AND r.isDeleted = false")
    List<ClaimReview> findByCaseIdAndReviewStatusNot(@Param("caseId") Long caseId, @Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.reviewStatus != :reviewStatus AND r.isDeleted = false")
    List<ClaimReview> findByReviewStatusNot(@Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.caseId = :caseId AND r.reviewStatus = :reviewStatus AND r.isDeleted = false")
    List<ClaimReview> findByCaseIdAndReviewStatus(@Param("caseId") Long caseId, @Param("reviewStatus") String reviewStatus);

    @Query("SELECT r FROM ClaimReview r WHERE r.reviewStatus = :reviewStatus AND r.isDeleted = false")
    List<ClaimReview> findByReviewStatus(@Param("reviewStatus") String reviewStatus);

    void deleteByCaseId(Long caseId);

    List<ClaimReview> findAllByCreditorNameAndIsDeletedFalse(String creditorName);
}
