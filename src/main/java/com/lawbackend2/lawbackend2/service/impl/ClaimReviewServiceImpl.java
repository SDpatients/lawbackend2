package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.ClaimReviewCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimReviewUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimReviewRepository;
import com.lawbackend2.lawbackend2.service.ClaimReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ClaimReviewServiceImpl implements ClaimReviewService {

    private final ClaimReviewRepository claimReviewRepository;
    private final ClaimRegistrationRepository claimRegistrationRepository;

    @Autowired
    public ClaimReviewServiceImpl(ClaimReviewRepository claimReviewRepository,
                                ClaimRegistrationRepository claimRegistrationRepository) {
        this.claimReviewRepository = claimReviewRepository;
        this.claimRegistrationRepository = claimRegistrationRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimReview createReview(ClaimReviewCreateRequest request, Long userId) {
        ClaimRegistration registration = claimRegistrationRepository.findById(request.getClaimRegistrationId())
                .orElseThrow(() -> new BusinessException("债权申报不存在"));

        Long count = claimReviewRepository.countByClaimRegistrationId(request.getClaimRegistrationId());
        Integer nextRound = count.intValue() + 1;

        ClaimReview review = new ClaimReview();
        BeanUtils.copyProperties(request, review);
        
        review.setCaseId(registration.getCaseId());
        review.setCreditorName(registration.getCreditorName());
        review.setReviewRound(nextRound);
        review.setCreateUserId(userId);
        review.setUpdateUserId(userId);
        
        if (review.getReviewDate() == null) {
            review.setReviewDate(LocalDateTime.now());
        }

        ClaimReview saved = claimReviewRepository.save(review);
        log.info("债权审查创建成功, reviewId: {}, claimRegistrationId: {}, reviewRound: {}", 
                  saved.getId(), request.getClaimRegistrationId(), nextRound);
        return saved;
    }

    @Override
    public ClaimReview getReviewById(Long reviewId) {
        return claimReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException("债权审查记录不存在"));
    }

    @Override
    public List<ClaimReview> getReviewListByClaimId(Long claimRegistrationId) {
        return claimReviewRepository.findAllByClaimRegistrationIdOrderByReviewRoundDesc(claimRegistrationId);
    }

    @Override
    public List<ClaimReview> getReviewListByCaseId(Long caseId, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "reviewDate"));
        Page<ClaimReview> page;
        if (caseId != null) {
            page = claimReviewRepository.findByCaseId(caseId, pageable);
        } else {
            page = claimReviewRepository.findAll(pageable);
        }
        return page.getContent();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimReview updateReview(Long reviewId, ClaimReviewUpdateRequest request) {
        ClaimReview review = getReviewById(reviewId);

        if (request.getReviewDate() != null) {
            review.setReviewDate(request.getReviewDate());
        }
        if (request.getReviewer() != null) {
            review.setReviewer(request.getReviewer());
        }
        if (request.getReviewBasis() != null) {
            review.setReviewBasis(request.getReviewBasis());
        }
        if (request.getConfirmedPrincipal() != null) {
            review.setConfirmedPrincipal(request.getConfirmedPrincipal());
        }
        if (request.getConfirmedInterest() != null) {
            review.setConfirmedInterest(request.getConfirmedInterest());
        }
        if (request.getConfirmedPenalty() != null) {
            review.setConfirmedPenalty(request.getConfirmedPenalty());
        }
        if (request.getConfirmedOtherLosses() != null) {
            review.setConfirmedOtherLosses(request.getConfirmedOtherLosses());
        }
        if (request.getConfirmedTotalAmount() != null) {
            review.setConfirmedTotalAmount(request.getConfirmedTotalAmount());
        }
        if (request.getUnconfirmedPrincipal() != null) {
            review.setUnconfirmedPrincipal(request.getUnconfirmedPrincipal());
        }
        if (request.getUnconfirmedInterest() != null) {
            review.setUnconfirmedInterest(request.getUnconfirmedInterest());
        }
        if (request.getUnconfirmedPenalty() != null) {
            review.setUnconfirmedPenalty(request.getUnconfirmedPenalty());
        }
        if (request.getUnconfirmedOtherLosses() != null) {
            review.setUnconfirmedOtherLosses(request.getUnconfirmedOtherLosses());
        }
        if (request.getUnconfirmedTotalAmount() != null) {
            review.setUnconfirmedTotalAmount(request.getUnconfirmedTotalAmount());
        }
        if (request.getAdjustmentReason() != null) {
            review.setAdjustmentReason(request.getAdjustmentReason());
        }
        if (request.getUnconfirmedReason() != null) {
            review.setUnconfirmedReason(request.getUnconfirmedReason());
        }
        if (request.getInsufficientEvidenceReason() != null) {
            review.setInsufficientEvidenceReason(request.getInsufficientEvidenceReason());
        }
        if (request.getExpiredReason() != null) {
            review.setExpiredReason(request.getExpiredReason());
        }
        if (request.getEvidenceAuthenticity() != null) {
            review.setEvidenceAuthenticity(request.getEvidenceAuthenticity());
        }
        if (request.getEvidenceRelevance() != null) {
            review.setEvidenceRelevance(request.getEvidenceRelevance());
        }
        if (request.getEvidenceLegality() != null) {
            review.setEvidenceLegality(request.getEvidenceLegality());
        }
        if (request.getEvidenceReviewNotes() != null) {
            review.setEvidenceReviewNotes(request.getEvidenceReviewNotes());
        }
        if (request.getConfirmedClaimNature() != null) {
            review.setConfirmedClaimNature(request.getConfirmedClaimNature());
        }
        if (request.getIsJointLiability() != null) {
            review.setIsJointLiability(request.getIsJointLiability() == 1);
        }
        if (request.getIsConditional() != null) {
            review.setIsConditional(request.getIsConditional() == 1);
        }
        if (request.getIsTerm() != null) {
            review.setIsTerm(request.getIsTerm() == 1);
        }
        if (request.getCollateralType() != null) {
            review.setCollateralType(request.getCollateralType());
        }
        if (request.getCollateralProperty() != null) {
            review.setCollateralProperty(request.getCollateralProperty());
        }
        if (request.getCollateralAmount() != null) {
            review.setCollateralAmount(request.getCollateralAmount());
        }
        if (request.getCollateralTerm() != null) {
            review.setCollateralTerm(request.getCollateralTerm());
        }
        if (request.getCollateralValidity() != null) {
            review.setCollateralValidity(request.getCollateralValidity());
        }
        if (request.getReviewConclusion() != null) {
            review.setReviewConclusion(request.getReviewConclusion());
        }
        if (request.getReviewSummary() != null) {
            review.setReviewSummary(request.getReviewSummary());
        }
        if (request.getReviewReport() != null) {
            review.setReviewReport(request.getReviewReport());
        }
        if (request.getReviewAttachments() != null) {
            review.setReviewAttachments(request.getReviewAttachments());
        }
        if (request.getReviewStatus() != null) {
            review.setReviewStatus(request.getReviewStatus());
        }
        if (request.getRemarks() != null) {
            review.setRemarks(request.getRemarks());
        }

        return claimReviewRepository.save(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long reviewId) {
        ClaimReview review = getReviewById(reviewId);
        review.setIsDeleted(true);
        claimReviewRepository.save(review);
        log.info("债权审查记录删除成功, reviewId: {}", reviewId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Long reviewId, Long userId) {
        ClaimReview review = getReviewById(reviewId);
        
        if (!"PENDING".equals(review.getReviewStatus()) && !"IN_PROGRESS".equals(review.getReviewStatus())) {
            throw new BusinessException("当前审查状态不允许提交");
        }

        if (review.getReviewConclusion() == null) {
            throw new BusinessException("请填写审查结论");
        }

        review.setReviewStatus("COMPLETED");
        review.setUpdateUserId(userId);
        claimReviewRepository.save(review);
        
        log.info("债权审查提交成功, reviewId: {}, claimRegistrationId: {}, reviewConclusion: {}", 
                  reviewId, review.getClaimRegistrationId(), review.getReviewConclusion());
    }

    @Override
    public List<ClaimReview> getPendingReviews(Long caseId) {
        return claimReviewRepository.findPendingReviewsByCaseId(caseId);
    }

    @Override
    public Long getReviewCount(Long caseId, String reviewStatus) {
        if (caseId != null && reviewStatus != null) {
            return claimReviewRepository    .countByCaseIdAndReviewStatus(caseId, reviewStatus);
        } else if (caseId != null) {
            return claimReviewRepository.countByCaseId(caseId);
        } else if (reviewStatus != null) {
            return claimReviewRepository.countByReviewStatus(reviewStatus);
        } else {
            return claimReviewRepository.count();
        }
    }
}
