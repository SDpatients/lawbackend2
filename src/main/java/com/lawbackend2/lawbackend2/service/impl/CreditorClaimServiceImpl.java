package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.CreditorClaimCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimReviewRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorClaim;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.dto.request.FundAccountCreateRequest;
import com.lawbackend2.lawbackend2.repository.CreditorClaimRepository;
import com.lawbackend2.lawbackend2.service.CreditorClaimService;
import com.lawbackend2.lawbackend2.service.FundAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CreditorClaimServiceImpl implements CreditorClaimService {

    private final CreditorClaimRepository creditorClaimRepository;
    private final FundAccountService fundAccountService;

    public CreditorClaimServiceImpl(CreditorClaimRepository creditorClaimRepository, FundAccountService fundAccountService) {
        this.creditorClaimRepository = creditorClaimRepository;
        this.fundAccountService = fundAccountService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreditorClaim createClaim(CreditorClaimCreateRequest request, Long userId) {
        // Check and create fund account if needed
        if (request.getAccountName() != null && !request.getAccountName().trim().isEmpty()) {
            // Check if fund account exists
            try {
                // Try to find fund account by name
                // Note: This will throw an exception if not found, which we'll catch
                fundAccountService.getFundAccountDetailByAccountName(request.getAccountName());
            } catch (Exception e) {
                // Fund account not found, create a new one
                FundAccountCreateRequest fundAccountRequest = new FundAccountCreateRequest();
                fundAccountRequest.setCaseId(request.getCaseId());
                fundAccountRequest.setCaseName(request.getCaseName() != null ? request.getCaseName() : "未知");
                fundAccountRequest.setAccountName(request.getAccountName());
                fundAccountRequest.setBankAccount(request.getCreditorBankAccount() != null ? request.getCreditorBankAccount() : "未知");
                fundAccountRequest.setBankName(request.getBankName() != null ? request.getBankName() : "未知");
                fundAccountRequest.setAccountType("未知");
                fundAccountRequest.setInitialBalance(java.math.BigDecimal.ZERO);
                
                // Create the fund account
                fundAccountService.createFundAccount(fundAccountRequest, userId);
                log.info("Created new fund account: {}", request.getAccountName());
            }
        }

        CreditorClaim creditorClaim = new CreditorClaim();
        BeanUtils.copyProperties(request, creditorClaim);
        creditorClaim.setCreateUserId(userId);
        creditorClaim.setUpdateUserId(userId);
        creditorClaim.setHasCourtJudgment(request.getHasCourtJudgment() != null && request.getHasCourtJudgment() == 1);
        creditorClaim.setHasExecution(request.getHasExecution() != null && request.getHasExecution() == 1);
        creditorClaim.setHasCollateral(request.getHasCollateral() != null && request.getHasCollateral() == 1);

        return creditorClaimRepository.save(creditorClaim);
    }

    @Override
    public CreditorClaim getClaimById(Long claimId) {
        return creditorClaimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException("债权申报不存在"));
    }

    @Override
    public List<CreditorClaim> getClaimList(Integer pageNum, Integer pageSize, Long caseId, String registrationStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<CreditorClaim> page;
        if (caseId != null && registrationStatus != null) {
            page = creditorClaimRepository.findByCaseIdAndRegistrationStatus(caseId, registrationStatus, pageable);
        } else if (caseId != null) {
            page = creditorClaimRepository.findByCaseId(caseId, pageable);
        } else if (registrationStatus != null) {
            page = creditorClaimRepository.findByRegistrationStatus(registrationStatus, pageable);
        } else {
            page = creditorClaimRepository.findAll(pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getClaimCount(Long caseId, String registrationStatus) {
        if (caseId != null && registrationStatus != null) {
            return creditorClaimRepository.findByCaseIdAndRegistrationStatus(caseId, registrationStatus, Pageable.unpaged()).getTotalElements();
        } else if (caseId != null) {
            return creditorClaimRepository.findByCaseId(caseId, Pageable.unpaged()).getTotalElements();
        } else if (registrationStatus != null) {
            return creditorClaimRepository.findByRegistrationStatus(registrationStatus, Pageable.unpaged()).getTotalElements();
        } else {
            return creditorClaimRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreditorClaim updateClaim(Long claimId, CreditorClaimUpdateRequest request) {
        CreditorClaim creditorClaim = getClaimById(claimId);

        if (request.getCreditorName() != null) {
            creditorClaim.setCreditorName(request.getCreditorName());
        }
        if (request.getPrincipal() != null) {
            creditorClaim.setPrincipal(request.getPrincipal());
        }
        if (request.getInterest() != null) {
            creditorClaim.setInterest(request.getInterest());
        }
        if (request.getPenalty() != null) {
            creditorClaim.setPenalty(request.getPenalty());
        }
        if (request.getOtherLosses() != null) {
            creditorClaim.setOtherLosses(request.getOtherLosses());
        }
        if (request.getTotalAmount() != null) {
            creditorClaim.setTotalAmount(request.getTotalAmount());
        }
        if (request.getClaimType() != null) {
            creditorClaim.setClaimType(request.getClaimType());
        }
        if (request.getClaimFacts() != null) {
            creditorClaim.setClaimFacts(request.getClaimFacts());
        }
        if (request.getRemarks() != null) {
            creditorClaim.setRemarks(request.getRemarks());
        }
        if (request.getClaimNature() != null) {
            creditorClaim.setClaimNature(request.getClaimNature());
        }
        if (request.getHasCourtJudgment() != null) {
            creditorClaim.setHasCourtJudgment(request.getHasCourtJudgment() == 1);
        }
        if (request.getHasExecution() != null) {
            creditorClaim.setHasExecution(request.getHasExecution() == 1);
        }
        if (request.getHasCollateral() != null) {
            creditorClaim.setHasCollateral(request.getHasCollateral() == 1);
        }

        return creditorClaimRepository.save(creditorClaim);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewClaim(Long claimId, CreditorClaimReviewRequest request, Long userId) {
        log.info("债权申报审核, claimId: {}, registrationStatus: {}, reviewerId: {}", 
                  claimId, request.getRegistrationStatus(), userId);

        CreditorClaim creditorClaim = getClaimById(claimId);

        if (!"PENDING".equals(creditorClaim.getRegistrationStatus())) {
            throw new BusinessException("债权申报当前状态不允许审核");
        }

        creditorClaim.setRegistrationStatus(request.getRegistrationStatus());
        creditorClaim.setUpdateUserId(userId);

        if (request.getClaimNatureManager() != null) {
            creditorClaim.setClaimNatureManager(request.getClaimNatureManager());
        }

        if ("REGISTERED".equals(request.getRegistrationStatus())) {
            log.info("债权申报审核通过, claimId: {}", claimId);
        } else if ("REJECTED".equals(request.getRegistrationStatus())) {
            log.info("债权申报审核驳回, claimId: {}", claimId);
        }

        creditorClaimRepository.save(creditorClaim);
        log.info("债权申报审核完成, claimId: {}, registrationStatus: {}", 
                  claimId, request.getRegistrationStatus());
    }

    @Override
    public CreditorClaim getClaimReviewStatus(Long claimId) {
        log.debug("查询债权申报审核状态, claimId: {}", claimId);
        CreditorClaim creditorClaim = getClaimById(claimId);
        return creditorClaim;
    }
}
