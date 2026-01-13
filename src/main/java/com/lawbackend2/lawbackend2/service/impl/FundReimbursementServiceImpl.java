package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementPaymentRequest;
import com.lawbackend2.lawbackend2.entity.FundReimbursement;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundReimbursementRepository;
import com.lawbackend2.lawbackend2.service.FundReimbursementService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class FundReimbursementServiceImpl implements FundReimbursementService {

    private final FundReimbursementRepository fundReimbursementRepository;

    public FundReimbursementServiceImpl(FundReimbursementRepository fundReimbursementRepository) {
        this.fundReimbursementRepository = fundReimbursementRepository;
    }

    @Override
    public Long createFundReimbursement(FundReimbursementCreateRequest request) {
        FundReimbursement reimbursement = new FundReimbursement();
        BeanUtils.copyProperties(request, reimbursement);
        reimbursement.setReimbursementNo(generateReimbursementNo());
        reimbursement.setApprovalStatus("PENDING");
        reimbursement.setPaymentStatus("UNPAID");
        reimbursement.setStatus("ACTIVE");

        if (reimbursement.getReimbursedAmount() == null) {
            reimbursement.setReimbursedAmount(BigDecimal.ZERO);
        }

        FundReimbursement saved = fundReimbursementRepository.save(reimbursement);
        return saved.getId();
    }

    @Override
    public PageResult<FundReimbursement> getFundReimbursementList(Integer pageNum, Integer pageSize, Long caseId, String reimbursementType, Long applicantId, String approvalStatus, String paymentStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<FundReimbursement> page = fundReimbursementRepository.findByConditions(caseId, reimbursementType, applicantId, approvalStatus, paymentStatus, pageable);

        PageResult<FundReimbursement> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public FundReimbursement getFundReimbursementDetail(Long reimbursementId) {
        return fundReimbursementRepository.findById(reimbursementId)
                .orElseThrow(() -> new BusinessException("费用报销不存在"));
    }

    @Override
    public void approveFundReimbursement(Long reimbursementId, FundReimbursementApprovalRequest request) {
        FundReimbursement reimbursement = getFundReimbursementDetail(reimbursementId);
        reimbursement.setApprovalStatus(request.getApprovalStatus());
        reimbursement.setApprovalOpinion(request.getApprovalOpinion());
        reimbursement.setApprovalDate(request.getApprovalDate() != null ? request.getApprovalDate() : LocalDateTime.now());

        if (request.getApprovedAmount() != null) {
            reimbursement.setApprovedAmount(request.getApprovedAmount());
        }

        fundReimbursementRepository.save(reimbursement);
    }

    @Override
    public void payFundReimbursement(Long reimbursementId, FundReimbursementPaymentRequest request) {
        FundReimbursement reimbursement = getFundReimbursementDetail(reimbursementId);

        if (!"APPROVED".equals(reimbursement.getApprovalStatus())) {
            throw new BusinessException("报销未审批通过，无法支付");
        }

        reimbursement.setPaymentStatus(request.getPaymentStatus());
        reimbursement.setReimbursedAmount(request.getReimbursedAmount());
        reimbursement.setPaymentMethod(request.getPaymentMethod());
        reimbursement.setPaymentAccountId(request.getPaymentAccountId());
        reimbursement.setPaymentVoucher(request.getPaymentVoucher());
        reimbursement.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());

        fundReimbursementRepository.save(reimbursement);
    }

    @Override
    public void deleteFundReimbursement(Long reimbursementId) {
        FundReimbursement reimbursement = getFundReimbursementDetail(reimbursementId);
        fundReimbursementRepository.delete(reimbursement);
    }

    private String generateReimbursementNo() {
        return "RMB" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}