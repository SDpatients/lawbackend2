package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.ClaimReviewCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimReviewUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.entity.User;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ClaimConfirmationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimReviewRepository;
import com.lawbackend2.lawbackend2.repository.UserRepository;
import com.lawbackend2.lawbackend2.service.ClaimReviewService;
import com.lawbackend2.lawbackend2.service.NotificationService;
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
    private final ClaimConfirmationRepository claimConfirmationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClaimReviewServiceImpl(ClaimReviewRepository claimReviewRepository,
                                ClaimRegistrationRepository claimRegistrationRepository,
                                ClaimConfirmationRepository claimConfirmationRepository,
                                UserRepository userRepository,
                                NotificationService notificationService,
                                ObjectMapper objectMapper) {
        this.claimReviewRepository = claimReviewRepository;
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.claimConfirmationRepository = claimConfirmationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
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
        
        // 处理reviewAttachments（List<String>转JSON字符串）
        if (request.getReviewAttachments() != null) {
            try {
                String attachmentsJson = objectMapper.writeValueAsString(request.getReviewAttachments());
                review.setReviewAttachments(attachmentsJson);
            } catch (JsonProcessingException e) {
                log.error("Failed to convert reviewAttachments to JSON", e);
                throw new BusinessException("附件处理失败");
            }
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
    public List<ClaimReview> getReviewListByCaseId(Long caseId, Integer pageNum, Integer pageSize, String reviewStatus) {
        if (reviewStatus != null && reviewStatus.trim().isEmpty()) {
            reviewStatus = null;
        }

        // 直接使用带条件的查询，避免获取所有记录后再过滤
        List<ClaimReview> allReviews;
        if (caseId != null && reviewStatus != null) {
            // 当明确指定了状态时，查询该状态的记录（但排除CONFIRMED）
            if (!"CONFIRMED".equals(reviewStatus)) {
                allReviews = claimReviewRepository.findByCaseIdAndReviewStatus(caseId, reviewStatus);
            } else {
                // 如果指定的是CONFIRMED，返回空列表
                allReviews = java.util.Collections.emptyList();
            }
        } else if (caseId != null) {
            // 当没有指定状态时，查询所有状态的记录（但排除CONFIRMED）
            allReviews = claimReviewRepository.findByCaseIdAndReviewStatusNot(caseId, "CONFIRMED");
        } else if (reviewStatus != null) {
            // 当明确指定了状态时，查询该状态的记录（但排除CONFIRMED）
            if (!"CONFIRMED".equals(reviewStatus)) {
                allReviews = claimReviewRepository.findByReviewStatus(reviewStatus);
            } else {
                // 如果指定的是CONFIRMED，返回空列表
                allReviews = java.util.Collections.emptyList();
            }
        } else {
            // 当没有指定状态时，查询所有状态的记录（但排除CONFIRMED）
            allReviews = claimReviewRepository.findByReviewStatusNot("CONFIRMED");
        }

        // 当明确指定状态为COMPLETED时，进一步过滤出registration_status=REVIEW_COMPLETED的记录
        if ("COMPLETED".equals(reviewStatus)) {
            allReviews = allReviews.stream()
                .filter(review -> {
                    ClaimRegistration registration = claimRegistrationRepository.findById(review.getClaimRegistrationId()).orElse(null);
                    return registration != null && "REVIEW_COMPLETED".equals(registration.getRegistrationStatus());
                })
                .collect(java.util.stream.Collectors.toList());
        }

        // 对结果进行排序和分页（处理null值）
        allReviews.sort((r1, r2) -> {
            if (r1.getReviewDate() == null && r2.getReviewDate() == null) {
                return 0;
            } else if (r1.getReviewDate() == null) {
                return 1; // 把null值放在后面
            } else if (r2.getReviewDate() == null) {
                return -1; // 把非null值放在前面
            } else {
                return r2.getReviewDate().compareTo(r1.getReviewDate());
            }
        });
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allReviews.size());
        if (start >= allReviews.size()) {
            return java.util.Collections.emptyList();
        }
        return allReviews.subList(start, end);
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
            try {
                String attachmentsJson = objectMapper.writeValueAsString(request.getReviewAttachments());
                review.setReviewAttachments(attachmentsJson);
            } catch (JsonProcessingException e) {
                log.error("Failed to convert reviewAttachments to JSON", e);
                throw new BusinessException("附件处理失败");
            }
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
    public void deleteReview(Long reviewId, Long userId) {
        ClaimReview review = getReviewById(reviewId);
        Long claimRegistrationId = review.getClaimRegistrationId();
        
        // 级联删除：检查并删除相关的债权确认记录
        List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(claimRegistrationId);
        for (ClaimConfirmation confirmation : confirmations) {
            confirmation.setIsDeleted(true);
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            log.info("级联删除债权确认记录, confirmationId: {}, claimRegistrationId: {}", confirmation.getId(), claimRegistrationId);
        }
        
        // 删除债权审查
        review.setIsDeleted(true);
        review.setUpdateUserId(userId);
        claimReviewRepository.save(review);
        log.info("债权审查记录删除成功, reviewId: {}", reviewId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Long reviewId, Long userId) {
        ClaimReview review = getReviewById(reviewId);
        
        if (review.getReviewConclusion() == null) {
            throw new BusinessException("请填写审查结论");
        }

        review.setReviewStatus("COMPLETED");
        review.setUpdateUserId(userId);
        claimReviewRepository.save(review);

        User user = userRepository.findById(userId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 完成了债权审查：%s", realName, review.getCreditorName());
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "债权审查通知",
                content,
                "CLAIM_REVIEW",
                review.getId(),
                "ClaimReview",
                userId,
                realName
        );

        log.info("债权审查提交成功, reviewId: {}, claimRegistrationId: {}, reviewConclusion: {}", 
                  reviewId, review.getClaimRegistrationId(), review.getReviewConclusion());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectReview(Long reviewId, String rejectReason, Long userId) {
        ClaimReview review = getReviewById(reviewId);
        Long claimRegistrationId = review.getClaimRegistrationId();
        
        // 设置审查结论为驳回
        review.setReviewConclusion("REJECTED");
        review.setReviewStatus("REJECTED");
        
        // 保存驳回理由到备注字段
        String remarks = review.getRemarks();
        String newRemarks = "驳回理由: " + rejectReason;
        if (remarks != null && !remarks.trim().isEmpty()) {
            newRemarks = remarks + "\n" + newRemarks;
        }
        review.setRemarks(newRemarks);
        
        review.setUpdateUserId(userId);
        claimReviewRepository.save(review);
        
        // 级联处理相关的债权确认记录
        List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(claimRegistrationId);
        for (ClaimConfirmation confirmation : confirmations) {
            confirmation.setIsDeleted(true);
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            log.info("级联处理审查驳回 - 删除确认记录, confirmationId: {}, claimRegistrationId: {}", confirmation.getId(), claimRegistrationId);
        }
        
        // 更新债权申报状态为驳回
        ClaimRegistration claimRegistration = claimRegistrationRepository.findById(claimRegistrationId).orElse(null);
        if (claimRegistration != null && !"REJECTED".equals(claimRegistration.getRegistrationStatus())) {
            claimRegistration.setRegistrationStatus("REJECTED");
            claimRegistration.setUpdateUserId(userId);
            claimRegistrationRepository.save(claimRegistration);
            log.info("更新债权申报状态为驳回, claimId: {}", claimRegistrationId);
        }
        
        // 发送通知
        User user = userRepository.findById(userId).orElse(null);
        String realName = user != null ? user.getRealName() : "未知用户";
        String content = String.format("%s 驳回了债权审查：%s\n驳回理由：%s", realName, review.getCreditorName(), rejectReason);
        notificationService.sendNotificationToAdminAndSuperAdmin(
                "债权审查驳回通知",
                content,
                "CLAIM_REVIEW_REJECT",
                review.getId(),
                "ClaimReview",
                userId,
                realName
        );
        
        log.info("债权审查驳回成功, reviewId: {}, creditorName: {}, rejectReason: {}", reviewId, review.getCreditorName(), rejectReason);
    }

    @Override
    public List<ClaimReview> getPendingReviews(Long caseId) {
        return claimReviewRepository.findPendingReviewsByCaseId(caseId);
    }

    @Override
    public Long getReviewCount(Long caseId, String reviewStatus) {
        if (reviewStatus != null && reviewStatus.trim().isEmpty()) {
            reviewStatus = null;
        }

        List<ClaimReview> allReviews;
        if (caseId != null && reviewStatus != null) {
            // 当明确指定了状态时，查询该状态的记录（但排除CONFIRMED）
            if (!"CONFIRMED".equals(reviewStatus)) {
                allReviews = claimReviewRepository.findByCaseIdAndReviewStatus(caseId, reviewStatus);
            } else {
                // 如果指定的是CONFIRMED，返回0
                return 0L;
            }
        } else if (caseId != null) {
            // 当没有指定状态时，查询所有状态的记录（但排除CONFIRMED）
            allReviews = claimReviewRepository.findByCaseIdAndReviewStatusNot(caseId, "CONFIRMED");
        } else if (reviewStatus != null) {
            // 当明确指定了状态时，查询该状态的记录（但排除CONFIRMED）
            if (!"CONFIRMED".equals(reviewStatus)) {
                allReviews = claimReviewRepository.findByReviewStatus(reviewStatus);
            } else {
                // 如果指定的是CONFIRMED，返回0
                return 0L;
            }
        } else {
            // 当没有指定状态时，查询所有状态的记录（但排除CONFIRMED）
            allReviews = claimReviewRepository.findByReviewStatusNot("CONFIRMED");
        }

        // 当明确指定状态为COMPLETED时，进一步过滤出registration_status=REVIEW_COMPLETED的记录数
        if ("COMPLETED".equals(reviewStatus)) {
            return allReviews.stream()
                .filter(review -> {
                    ClaimRegistration registration = claimRegistrationRepository.findById(review.getClaimRegistrationId()).orElse(null);
                    return registration != null && "REVIEW_COMPLETED".equals(registration.getRegistrationStatus());
                })
                .count();
        }

        return (long) allReviews.size();
    }
}
