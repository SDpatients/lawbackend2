package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ClaimConfirmationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimReviewRepository;
import com.lawbackend2.lawbackend2.service.ClaimRegistrationService;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ClaimRegistrationServiceImpl implements ClaimRegistrationService {

    private final ClaimRegistrationRepository claimRegistrationRepository;
    private final ClaimReviewRepository claimReviewRepository;
    private final ClaimConfirmationRepository claimConfirmationRepository;

    @Autowired
    public ClaimRegistrationServiceImpl(ClaimRegistrationRepository claimRegistrationRepository,
                                      ClaimReviewRepository claimReviewRepository,
                                      ClaimConfirmationRepository claimConfirmationRepository) {
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.claimReviewRepository = claimReviewRepository;
        this.claimConfirmationRepository = claimConfirmationRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimRegistration createClaim(ClaimRegistrationCreateRequest request, Long userId) {
        ClaimRegistration claimRegistration = new ClaimRegistration();
        BeanUtils.copyProperties(request, claimRegistration);
        
        claimRegistration.setCreateUserId(userId);
        claimRegistration.setUpdateUserId(userId);
        claimRegistration.setHasCourtJudgment(request.getHasCourtJudgment() != null && request.getHasCourtJudgment() == 1);
        claimRegistration.setHasExecution(request.getHasExecution() != null && request.getHasExecution() == 1);
        claimRegistration.setHasCollateral(request.getHasCollateral() != null && request.getHasCollateral() == 1);
        
        if (claimRegistration.getRegistrationDate() == null) {
            claimRegistration.setRegistrationDate(LocalDateTime.now());
        }
        
        String claimNo = generateClaimNo();
        claimRegistration.setClaimNo(claimNo);
        
        ClaimRegistration saved = claimRegistrationRepository.save(claimRegistration);
        
        createInitialReviewAndConfirmation(saved, userId);
        
        log.info("债权申报创建成功, claimId: {}, claimNo: {}", saved.getId(), claimNo);
        return saved;
    }

    @Override
    public ClaimRegistration getClaimById(Long claimId) {
        return claimRegistrationRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException("债权申报不存在"));
    }

    @Override
    public ClaimDetailResponse getClaimDetailById(Long claimId) {
        ClaimRegistration registration = getClaimById(claimId);
        
        ClaimDetailResponse response = new ClaimDetailResponse();
        BeanUtils.copyProperties(registration, response);
        
        Optional<ClaimReview> reviewOpt = claimReviewRepository.findFirstByClaimRegistrationIdOrderByReviewRoundDesc(claimId);
        if (reviewOpt.isPresent()) {
            ClaimReview review = reviewOpt.get();
            ClaimDetailResponse.ClaimReviewInfo reviewInfo = new ClaimDetailResponse.ClaimReviewInfo();
            BeanUtils.copyProperties(review, reviewInfo);
            response.setReviewInfo(reviewInfo);
        }
        
        Optional<ClaimConfirmation> confirmationOpt = claimConfirmationRepository.findByClaimRegistrationId(claimId);
        if (confirmationOpt.isPresent()) {
            ClaimConfirmation confirmation = confirmationOpt.get();
            ClaimDetailResponse.ClaimConfirmationInfo confirmationInfo = new ClaimDetailResponse.ClaimConfirmationInfo();
            BeanUtils.copyProperties(confirmation, confirmationInfo);
            response.setConfirmationInfo(confirmationInfo);
        }
        
        return response;
    }

    @Override
    public List<ClaimRegistration> getClaimList(Integer pageNum, Integer pageSize, Long caseId, String registrationStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<ClaimRegistration> page;
        if (caseId != null && registrationStatus != null) {
            page = claimRegistrationRepository.findByCaseIdAndRegistrationStatus(caseId, registrationStatus, pageable);
        } else if (caseId != null) {
            page = claimRegistrationRepository.findByCaseId(caseId, pageable);
        } else if (registrationStatus != null) {
            page = claimRegistrationRepository.findByRegistrationStatus(registrationStatus, pageable);
        } else {
            page = claimRegistrationRepository.findAll(pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getClaimCount(Long caseId, String registrationStatus) {
        if (caseId != null && registrationStatus != null) {
            return claimRegistrationRepository.findByCaseIdAndRegistrationStatus(caseId, registrationStatus, Pageable.unpaged()).getTotalElements();
        } else if (caseId != null) {
            return claimRegistrationRepository.findByCaseId(caseId, Pageable.unpaged()).getTotalElements();
        } else if (registrationStatus != null) {
            return claimRegistrationRepository.findByRegistrationStatus(registrationStatus, Pageable.unpaged()).getTotalElements();
        } else {
            return claimRegistrationRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClaimRegistration updateClaim(Long claimId, ClaimRegistrationUpdateRequest request) {
        ClaimRegistration claimRegistration = getClaimById(claimId);

        if (request.getCreditorName() != null) {
            claimRegistration.setCreditorName(request.getCreditorName());
        }
        if (request.getCreditorType() != null) {
            claimRegistration.setCreditorType(request.getCreditorType());
        }
        if (request.getCreditCode() != null) {
            claimRegistration.setCreditCode(request.getCreditCode());
        }
        if (request.getLegalRepresentative() != null) {
            claimRegistration.setLegalRepresentative(request.getLegalRepresentative());
        }
        if (request.getServiceAddress() != null) {
            claimRegistration.setServiceAddress(request.getServiceAddress());
        }
        if (request.getAgentName() != null) {
            claimRegistration.setAgentName(request.getAgentName());
        }
        if (request.getAgentPhone() != null) {
            claimRegistration.setAgentPhone(request.getAgentPhone());
        }
        if (request.getAgentIdCard() != null) {
            claimRegistration.setAgentIdCard(request.getAgentIdCard());
        }
        if (request.getAgentAddress() != null) {
            claimRegistration.setAgentAddress(request.getAgentAddress());
        }
        if (request.getAccountName() != null) {
            claimRegistration.setAccountName(request.getAccountName());
        }
        if (request.getCreditorBankAccount() != null) {
            claimRegistration.setCreditorBankAccount(request.getCreditorBankAccount());
        }
        if (request.getBankName() != null) {
            claimRegistration.setBankName(request.getBankName());
        }
        if (request.getPrincipal() != null) {
            claimRegistration.setPrincipal(request.getPrincipal());
        }
        if (request.getInterest() != null) {
            claimRegistration.setInterest(request.getInterest());
        }
        if (request.getPenalty() != null) {
            claimRegistration.setPenalty(request.getPenalty());
        }
        if (request.getOtherLosses() != null) {
            claimRegistration.setOtherLosses(request.getOtherLosses());
        }
        if (request.getTotalAmount() != null) {
            claimRegistration.setTotalAmount(request.getTotalAmount());
        }
        if (request.getHasCourtJudgment() != null) {
            claimRegistration.setHasCourtJudgment(request.getHasCourtJudgment() == 1);
        }
        if (request.getHasExecution() != null) {
            claimRegistration.setHasExecution(request.getHasExecution() == 1);
        }
        if (request.getHasCollateral() != null) {
            claimRegistration.setHasCollateral(request.getHasCollateral() == 1);
        }
        if (request.getClaimNature() != null) {
            claimRegistration.setClaimNature(request.getClaimNature());
        }
        if (request.getClaimType() != null) {
            claimRegistration.setClaimType(request.getClaimType());
        }
        if (request.getClaimFacts() != null) {
            claimRegistration.setClaimFacts(request.getClaimFacts());
        }
        if (request.getClaimIdentifier() != null) {
            claimRegistration.setClaimIdentifier(request.getClaimIdentifier());
        }
        if (request.getEvidenceList() != null) {
            claimRegistration.setEvidenceList(request.getEvidenceList());
        }
        if (request.getEvidenceMaterials() != null) {
            claimRegistration.setEvidenceMaterials(request.getEvidenceMaterials());
        }
        if (request.getEvidenceAttachments() != null) {
            claimRegistration.setEvidenceAttachments(request.getEvidenceAttachments());
        }
        if (request.getRegistrationDate() != null) {
            claimRegistration.setRegistrationDate(request.getRegistrationDate());
        }
        if (request.getRegistrationDeadline() != null) {
            claimRegistration.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getMaterialReceiver() != null) {
            claimRegistration.setMaterialReceiver(request.getMaterialReceiver());
        }
        if (request.getMaterialReceiveDate() != null) {
            claimRegistration.setMaterialReceiveDate(request.getMaterialReceiveDate());
        }
        if (request.getMaterialCompleteness() != null) {
            claimRegistration.setMaterialCompleteness(request.getMaterialCompleteness());
        }
        if (request.getRegistrationStatus() != null) {
            claimRegistration.setRegistrationStatus(request.getRegistrationStatus());
        }
        if (request.getRemarks() != null) {
            claimRegistration.setRemarks(request.getRemarks());
        }

        return claimRegistrationRepository.save(claimRegistration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteClaim(Long claimId) {
        ClaimRegistration claimRegistration = getClaimById(claimId);
        claimRegistration.setIsDeleted(true);
        claimRegistrationRepository.save(claimRegistration);
        log.info("债权申报删除成功, claimId: {}", claimId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRegistrationStatus(Long claimId, String status, Long userId) {
        ClaimRegistration claimRegistration = getClaimById(claimId);
        claimRegistration.setRegistrationStatus(status);
        claimRegistration.setUpdateUserId(userId);
        claimRegistrationRepository.save(claimRegistration);
        log.info("债权申报状态更新成功, claimId: {}, status: {}", claimId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveMaterial(Long claimId, String receiver, String completeness, Long userId) {
        ClaimRegistration claimRegistration = getClaimById(claimId);
        claimRegistration.setMaterialReceiver(receiver);
        claimRegistration.setMaterialReceiveDate(LocalDateTime.now());
        claimRegistration.setMaterialCompleteness(completeness);
        claimRegistration.setUpdateUserId(userId);
        claimRegistrationRepository.save(claimRegistration);
        log.info("债权申报材料接收成功, claimId: {}, receiver: {}, completeness: {}", claimId, receiver, completeness);
    }

    private String generateClaimNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = claimRegistrationRepository.count() + 1;
        return "CLAIM" + dateStr + String.format("%06d", count);
    }

    private void createInitialReviewAndConfirmation(ClaimRegistration registration, Long userId) {
        ClaimReview review = new ClaimReview();
        review.setClaimRegistrationId(registration.getId());
        review.setCaseId(registration.getCaseId());
        review.setCreditorName(registration.getCreditorName());
        review.setDeclaredPrincipal(registration.getPrincipal());
        review.setDeclaredInterest(registration.getInterest());
        review.setDeclaredPenalty(registration.getPenalty());
        review.setDeclaredOtherLosses(registration.getOtherLosses());
        review.setDeclaredTotalAmount(registration.getTotalAmount());
        review.setReviewRound(1);
        review.setReviewStatus("PENDING");
        review.setCreateUserId(userId);
        review.setUpdateUserId(userId);
        claimReviewRepository.save(review);

        ClaimConfirmation confirmation = new ClaimConfirmation();
        confirmation.setClaimRegistrationId(registration.getId());
        confirmation.setCaseId(registration.getCaseId());
        confirmation.setCreditorName(registration.getCreditorName());
        confirmation.setFinalConfirmedAmount(registration.getTotalAmount());
        confirmation.setConfirmationStatus("PENDING");
        confirmation.setCreateUserId(userId);
        confirmation.setUpdateUserId(userId);
        claimConfirmationRepository.save(confirmation);
    }
}
