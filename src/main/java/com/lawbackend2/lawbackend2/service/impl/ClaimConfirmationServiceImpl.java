package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.ClaimConfirmationCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimConfirmationUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ClaimConfirmationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimReviewRepository;
import com.lawbackend2.lawbackend2.service.ClaimConfirmationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

@Slf4j
@Service
public class ClaimConfirmationServiceImpl implements ClaimConfirmationService {

    private final ClaimConfirmationRepository claimConfirmationRepository;
    private final ClaimRegistrationRepository claimRegistrationRepository;
    private final ClaimReviewRepository claimReviewRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ClaimConfirmationServiceImpl(ClaimConfirmationRepository claimConfirmationRepository,
                                      ClaimRegistrationRepository claimRegistrationRepository,
                                      ClaimReviewRepository claimReviewRepository) {
        this.claimConfirmationRepository = claimConfirmationRepository;
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.claimReviewRepository = claimReviewRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimConfirmation createConfirmation(ClaimConfirmationCreateRequest request, Long userId) {
        ClaimRegistration registration = claimRegistrationRepository.findById(request.getClaimRegistrationId())
                .orElseThrow(() -> new BusinessException("债权申报不存在"));

        ClaimConfirmation confirmation = new ClaimConfirmation();
        BeanUtils.copyProperties(request, confirmation);
        
        // 将 List<String> 转为 JSON 字符串存储
        if (request.getConfirmationAttachments() != null) {
            try {
                String attachmentsJson = objectMapper.writeValueAsString(request.getConfirmationAttachments());
                confirmation.setConfirmationAttachments(attachmentsJson);
            } catch (Exception e) {
                log.warn("附件列表序列化失败", e);
            }
        }
        
        confirmation.setCaseId(registration.getCaseId());
        confirmation.setCreditorName(registration.getCreditorName());
        confirmation.setCreateUserId(userId);
        confirmation.setUpdateUserId(userId);

        ClaimConfirmation saved = claimConfirmationRepository.save(confirmation);
        
        // 自动同步债权审查数据
        syncReviewDataToConfirmation(saved.getId(), userId);
        
        log.info("债权确认记录创建成功，confirmationId: {}, claimRegistrationId: {}", 
                  saved.getId(), request.getClaimRegistrationId());
        return saved;
    }

    @Override
    public ClaimConfirmation getConfirmationById(Long confirmationId) {
        return claimConfirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new BusinessException("债权确认记录不存在"));
    }

    @Override
    public ClaimConfirmation getConfirmationByClaimId(Long claimRegistrationId) {
        List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationIdAndIsDeletedFalse(claimRegistrationId);
        if (confirmations.isEmpty()) {
            throw new BusinessException("债权确认记录不存在");
        }
        return confirmations.get(0);
    }

    @Override
    public List<ClaimConfirmation> getConfirmationListByCaseId(Long caseId, Integer pageNum, Integer pageSize, String confirmationStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<ClaimConfirmation> page;
        if (confirmationStatus == null || confirmationStatus.trim().isEmpty()) {
            if (caseId != null) {
                page = claimConfirmationRepository.findByCaseIdAndIsDeletedFalse(caseId, pageable);
            } else {
                page = claimConfirmationRepository.findAll(pageable);
            }
        } else {
            if (caseId != null && confirmationStatus != null) {
                page = claimConfirmationRepository.findByCaseIdAndConfirmationStatusAndIsDeletedFalse(caseId, confirmationStatus, pageable);
            } else if (caseId != null) {
                page = claimConfirmationRepository.findByCaseIdAndIsDeletedFalse(caseId, pageable);
            } else if (confirmationStatus != null) {
                page = claimConfirmationRepository.findByConfirmationStatusAndIsDeletedFalse(confirmationStatus, pageable);
            } else {
                page = claimConfirmationRepository.findAll(pageable);
            }
        }
        return page.getContent();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimConfirmation updateConfirmation(Long confirmationId, ClaimConfirmationUpdateRequest request) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);

        if (request.getMeetingType() != null) {
            confirmation.setMeetingType(request.getMeetingType());
        }
        if (request.getMeetingDate() != null) {
            confirmation.setMeetingDate(request.getMeetingDate());
        }
        if (request.getMeetingLocation() != null) {
            confirmation.setMeetingLocation(request.getMeetingLocation());
        }
        if (request.getVoteResult() != null) {
            confirmation.setVoteResult(request.getVoteResult());
        }
        if (request.getVoteNotes() != null) {
            confirmation.setVoteNotes(request.getVoteNotes());
        }
        if (request.getHasObjection() != null) {
            confirmation.setHasObjection(request.getHasObjection());
        }
        if (request.getObjector() != null) {
            confirmation.setObjector(request.getObjector());
        }
        if (request.getObjectionReason() != null) {
            confirmation.setObjectionReason(request.getObjectionReason());
        }
        if (request.getObjectionAmount() != null) {
            confirmation.setObjectionAmount(request.getObjectionAmount());
        }
        if (request.getObjectionDate() != null) {
            confirmation.setObjectionDate(request.getObjectionDate());
        }
        if (request.getNegotiationResult() != null) {
            confirmation.setNegotiationResult(request.getNegotiationResult());
        }
        if (request.getNegotiationDate() != null) {
            confirmation.setNegotiationDate(request.getNegotiationDate());
        }
        if (request.getNegotiationParticipants() != null) {
            confirmation.setNegotiationParticipants(request.getNegotiationParticipants());
        }
        if (request.getCourtRulingDate() != null) {
            confirmation.setCourtRulingDate(request.getCourtRulingDate());
        }
        if (request.getCourtRulingNo() != null) {
            confirmation.setCourtRulingNo(request.getCourtRulingNo());
        }
        if (request.getCourtRulingResult() != null) {
            confirmation.setCourtRulingResult(request.getCourtRulingResult());
        }
        if (request.getCourtRulingAmount() != null) {
            confirmation.setCourtRulingAmount(request.getCourtRulingAmount());
        }
        if (request.getCourtRulingNotes() != null) {
            confirmation.setCourtRulingNotes(request.getCourtRulingNotes());
        }
        if (request.getHasLawsuit() != null) {
            confirmation.setHasLawsuit(request.getHasLawsuit());
        }
        if (request.getLawsuitCaseNo() != null) {
            confirmation.setLawsuitCaseNo(request.getLawsuitCaseNo());
        }
        if (request.getLawsuitStatus() != null) {
            confirmation.setLawsuitStatus(request.getLawsuitStatus());
        }
        if (request.getLawsuitResult() != null) {
            confirmation.setLawsuitResult(request.getLawsuitResult());
        }
        if (request.getLawsuitAmount() != null) {
            confirmation.setLawsuitAmount(request.getLawsuitAmount());
        }
        if (request.getLawsuitNotes() != null) {
            confirmation.setLawsuitNotes(request.getLawsuitNotes());
        }
        if (request.getFinalConfirmedAmount() != null) {
            confirmation.setFinalConfirmedAmount(request.getFinalConfirmedAmount());
        }
        if (request.getFinalConfirmationDate() != null) {
            confirmation.setFinalConfirmationDate(request.getFinalConfirmationDate());
        }
        if (request.getFinalConfirmationBasis() != null) {
            confirmation.setFinalConfirmationBasis(request.getFinalConfirmationBasis());
        }
        
        // 处理附件列表更新
        if (request.getConfirmationAttachments() != null) {
            try {
                String attachmentsJson = objectMapper.writeValueAsString(request.getConfirmationAttachments());
                confirmation.setConfirmationAttachments(attachmentsJson);
            } catch (Exception e) {
                log.warn("附件列表序列化失败", e);
            }
        }
        
        if (request.getConfirmationStatus() != null) {
            confirmation.setConfirmationStatus(request.getConfirmationStatus());
        }
        if (request.getRemarks() != null) {
            confirmation.setRemarks(request.getRemarks());
        }

        // 更新债权审查相关字段
        if (request.getDeclaredPrincipal() != null) {
            confirmation.setDeclaredPrincipal(request.getDeclaredPrincipal());
        }
        if (request.getDeclaredInterest() != null) {
            confirmation.setDeclaredInterest(request.getDeclaredInterest());
        }
        if (request.getDeclaredPenalty() != null) {
            confirmation.setDeclaredPenalty(request.getDeclaredPenalty());
        }
        if (request.getDeclaredOtherLosses() != null) {
            confirmation.setDeclaredOtherLosses(request.getDeclaredOtherLosses());
        }
        if (request.getDeclaredTotalAmount() != null) {
            confirmation.setDeclaredTotalAmount(request.getDeclaredTotalAmount());
        }
        if (request.getConfirmedPrincipal() != null) {
            confirmation.setConfirmedPrincipal(request.getConfirmedPrincipal());
        }
        if (request.getConfirmedInterest() != null) {
            confirmation.setConfirmedInterest(request.getConfirmedInterest());
        }
        if (request.getConfirmedPenalty() != null) {
            confirmation.setConfirmedPenalty(request.getConfirmedPenalty());
        }
        if (request.getConfirmedOtherLosses() != null) {
            confirmation.setConfirmedOtherLosses(request.getConfirmedOtherLosses());
        }
        if (request.getConfirmedTotalAmount() != null) {
            confirmation.setConfirmedTotalAmount(request.getConfirmedTotalAmount());
        }
        if (request.getUnconfirmedPrincipal() != null) {
            confirmation.setUnconfirmedPrincipal(request.getUnconfirmedPrincipal());
        }
        if (request.getUnconfirmedInterest() != null) {
            confirmation.setUnconfirmedInterest(request.getUnconfirmedInterest());
        }
        if (request.getUnconfirmedPenalty() != null) {
            confirmation.setUnconfirmedPenalty(request.getUnconfirmedPenalty());
        }
        if (request.getUnconfirmedOtherLosses() != null) {
            confirmation.setUnconfirmedOtherLosses(request.getUnconfirmedOtherLosses());
        }
        if (request.getUnconfirmedTotalAmount() != null) {
            confirmation.setUnconfirmedTotalAmount(request.getUnconfirmedTotalAmount());
        }

        confirmation.setUpdateUserId(1L); // TODO: 从上下文获取实际用户 ID

        ClaimConfirmation updated = claimConfirmationRepository.save(confirmation);
        log.info("债权确认记录更新成功，confirmationId: {}", confirmationId);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfirmation(Long confirmationId) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);
        confirmation.setIsDeleted(true);
        claimConfirmationRepository.save(confirmation);
        log.info("债权确认记录删除成功, confirmationId: {}", confirmationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitObjection(Long confirmationId, Long userId) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);
        
        if (!"CONFIRMED".equals(confirmation.getConfirmationStatus())) {
            throw new BusinessException("当前确认状态不允许提交异议");
        }

        confirmation.setHasObjection(true);
        confirmation.setObjectionDate(LocalDateTime.now());
        confirmation.setConfirmationStatus("OBJECTION");
        confirmation.setUpdateUserId(userId);
        claimConfirmationRepository.save(confirmation);
        
        log.info("债权异议提交成功, confirmationId: {}, claimRegistrationId: {}", 
                  confirmationId, confirmation.getClaimRegistrationId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleNegotiation(Long confirmationId, String result, Long userId) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);
        
        if (!"OBJECTION".equals(confirmation.getConfirmationStatus())) {
            throw new BusinessException("当前状态不允许协商处理");
        }

        confirmation.setNegotiationResult(result);
        confirmation.setNegotiationDate(LocalDateTime.now());
        confirmation.setConfirmationStatus("CONFIRMED");
        confirmation.setUpdateUserId(userId);
        claimConfirmationRepository.save(confirmation);
        
        log.info("债权协商处理成功, confirmationId: {}, result: {}", confirmationId, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitCourtRuling(Long confirmationId, Long userId) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);
        
        if (confirmation.getCourtRulingNo() == null || confirmation.getCourtRulingResult() == null) {
            throw new BusinessException("请填写法院裁定信息");
        }

        confirmation.setConfirmationStatus("CONFIRMED");
        confirmation.setFinalConfirmationBasis("COURT");
        confirmation.setFinalConfirmationDate(LocalDateTime.now());
        confirmation.setUpdateUserId(userId);
        claimConfirmationRepository.save(confirmation);
        
        log.info("法院裁定提交成功, confirmationId: {}, courtRulingNo: {}", 
                  confirmationId, confirmation.getCourtRulingNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLawsuitStatus(Long confirmationId, String status, Long userId) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);
        
        if (!confirmation.getHasLawsuit()) {
            throw new BusinessException("该债权未提起诉讼");
        }

        confirmation.setLawsuitStatus(status);
        confirmation.setUpdateUserId(userId);
        claimConfirmationRepository.save(confirmation);
        
        log.info("诉讼状态更新成功, confirmationId: {}, status: {}", confirmationId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finalizeConfirmation(Long confirmationId, Long userId) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);
        
        if (!"CONFIRMED".equals(confirmation.getConfirmationStatus())) {
            throw new BusinessException("当前状态不允许最终确认");
        }

        if (confirmation.getFinalConfirmedAmount() == null) {
            Optional<ClaimReview> reviewOpt = claimReviewRepository
                    .findFirstByClaimRegistrationIdAndIsDeletedFalseOrderByReviewRoundDesc(confirmation.getClaimRegistrationId());
            if (reviewOpt.isPresent()) {
                ClaimReview review = reviewOpt.get();
                confirmation.setFinalConfirmedAmount(review.getConfirmedTotalAmount());
            } else {
                throw new BusinessException("无法确定最终确认金额");
            }
        }

        if (confirmation.getFinalConfirmationDate() == null) {
            confirmation.setFinalConfirmationDate(LocalDateTime.now());
        }

        if (confirmation.getFinalConfirmationBasis() == null) {
            confirmation.setFinalConfirmationBasis("MEETING");
        }

        confirmation.setConfirmationStatus("CONFIRMED");
        confirmation.setUpdateUserId(userId);
        claimConfirmationRepository.save(confirmation);
        
        log.info("债权最终确认成功, confirmationId: {}, finalConfirmedAmount: {}", 
                  confirmationId, confirmation.getFinalConfirmedAmount());
    }

    @Override
    public List<ClaimConfirmation> getObjectionsByCaseId(Long caseId) {
        return claimConfirmationRepository.findObjectionsByCaseId(caseId);
    }

    @Override
    public List<ClaimConfirmation> getLawsuitsByCaseId(Long caseId) {
        return claimConfirmationRepository.findLawsuitsByCaseId(caseId);
    }

    @Override
    public List<ClaimConfirmation> getPendingConfirmations(Long caseId) {
        return claimConfirmationRepository.findPendingConfirmationsByCaseId(caseId);
    }

    @Override
    public Long getConfirmationCount(Long caseId, String confirmationStatus) {
        if (confirmationStatus == null || confirmationStatus.trim().isEmpty()) {
            if (caseId != null) {
                return claimConfirmationRepository.countByCaseId(caseId);
            } else {
                return claimConfirmationRepository.count();
            }
        }

        if (caseId != null && confirmationStatus != null) {
            return claimConfirmationRepository.countByCaseIdAndConfirmationStatus(caseId, confirmationStatus);
        } else if (caseId != null) {
            return claimConfirmationRepository.countByCaseId(caseId);
        } else if (confirmationStatus != null) {
            return claimConfirmationRepository.countByConfirmationStatus(confirmationStatus);
        } else {
            return claimConfirmationRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncReviewDataToConfirmation(Long confirmationId, Long userId) {
        ClaimConfirmation confirmation = getConfirmationById(confirmationId);
        
        Optional<ClaimReview> reviewOpt = claimReviewRepository
                .findFirstByClaimRegistrationIdAndIsDeletedFalseOrderByReviewRoundDesc(confirmation.getClaimRegistrationId());
        
        if (reviewOpt.isPresent()) {
            ClaimReview review = reviewOpt.get();
            
            confirmation.setDeclaredPrincipal(review.getDeclaredPrincipal());
            confirmation.setDeclaredInterest(review.getDeclaredInterest());
            confirmation.setDeclaredPenalty(review.getDeclaredPenalty());
            confirmation.setDeclaredOtherLosses(review.getDeclaredOtherLosses());
            confirmation.setDeclaredTotalAmount(review.getDeclaredTotalAmount());
            
            confirmation.setConfirmedPrincipal(review.getConfirmedPrincipal());
            confirmation.setConfirmedInterest(review.getConfirmedInterest());
            confirmation.setConfirmedPenalty(review.getConfirmedPenalty());
            confirmation.setConfirmedOtherLosses(review.getConfirmedOtherLosses());
            confirmation.setConfirmedTotalAmount(review.getConfirmedTotalAmount());
            
            confirmation.setUnconfirmedPrincipal(review.getUnconfirmedPrincipal());
            confirmation.setUnconfirmedInterest(review.getUnconfirmedInterest());
            confirmation.setUnconfirmedPenalty(review.getUnconfirmedPenalty());
            confirmation.setUnconfirmedOtherLosses(review.getUnconfirmedOtherLosses());
            confirmation.setUnconfirmedTotalAmount(review.getUnconfirmedTotalAmount());
            
            if (review.getConfirmedTotalAmount() != null) {
                confirmation.setFinalConfirmedAmount(review.getConfirmedTotalAmount());
            }
            
            confirmation.setUpdateUserId(userId);
            claimConfirmationRepository.save(confirmation);
            
            log.info("债权审查数据同步到确认表成功，confirmationId: {}, claimRegistrationId: {}", 
                      confirmationId, confirmation.getClaimRegistrationId());
        } else {
            log.warn("未找到对应的债权审查数据，confirmationId: {}, claimRegistrationId: {}", 
                      confirmationId, confirmation.getClaimRegistrationId());
        }
    }
}
