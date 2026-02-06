package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ClaimReviewCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimReviewUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimReview;

import java.util.List;

public interface ClaimReviewService {
    ClaimReview createReview(ClaimReviewCreateRequest request, Long userId);

    ClaimReview getReviewById(Long reviewId);

    List<ClaimReview> getReviewListByClaimId(Long claimRegistrationId);

    List<ClaimReview> getReviewListByCaseId(Long caseId, Integer pageNum, Integer pageSize, String reviewStatus);

    ClaimReview updateReview(Long reviewId, ClaimReviewUpdateRequest request);

    void deleteReview(Long reviewId, Long userId);

    void submitReview(Long reviewId, Long userId);

    void rejectReview(Long reviewId, String rejectReason, Long userId);

    List<ClaimReview> getPendingReviews(Long caseId);

    Long getReviewCount(Long caseId, String reviewStatus);
}
