package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtRepaymentRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CommonDebt;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CommonDebtRepository;
import com.lawbackend2.lawbackend2.service.CommonDebtService;
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
public class CommonDebtServiceImpl implements CommonDebtService {

    private final CommonDebtRepository commonDebtRepository;

    public CommonDebtServiceImpl(CommonDebtRepository commonDebtRepository) {
        this.commonDebtRepository = commonDebtRepository;
    }

    @Override
    public Long createCommonDebt(CommonDebtCreateRequest request) {
        CommonDebt debt = new CommonDebt();
        BeanUtils.copyProperties(request, debt);
        debt.setDebtNo(generateDebtNo());
        debt.setApprovalStatus("PENDING");
        debt.setRepaymentStatus("UNREPAID");
        debt.setIsOverdue(false);
        debt.setStatus("ACTIVE");

        if (debt.getRepaidAmount() == null) {
            debt.setRepaidAmount(BigDecimal.ZERO);
        }

        CommonDebt saved = commonDebtRepository.save(debt);
        return saved.getId();
    }

    @Override
    public PageResult<CommonDebt> getCommonDebtList(Integer pageNum, Integer pageSize, Long caseId, String debtType, String approvalStatus, String repaymentStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<CommonDebt> page = commonDebtRepository.findByConditions(caseId, debtType, approvalStatus, repaymentStatus, pageable);

        PageResult<CommonDebt> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public CommonDebt getCommonDebtDetail(Long debtId) {
        return commonDebtRepository.findById(debtId)
                .orElseThrow(() -> new BusinessException("共益债务不存在"));
    }

    @Override
    public void updateCommonDebt(Long debtId, CommonDebtUpdateRequest request) {
        CommonDebt debt = getCommonDebtDetail(debtId);
        BeanUtils.copyProperties(request, debt, "id", "debtNo", "caseId", "caseName", "approvalStatus", "repaymentStatus");
        commonDebtRepository.save(debt);
    }

    @Override
    public void approveCommonDebt(Long debtId, CommonDebtApprovalRequest request) {
        CommonDebt debt = getCommonDebtDetail(debtId);
        debt.setApprovalStatus(request.getApprovalStatus());
        debt.setApprovalOpinion(request.getApprovalOpinion());
        debt.setApprovalDate(request.getApprovalDate() != null ? request.getApprovalDate() : LocalDateTime.now());

        if ("APPROVED".equals(request.getApprovalStatus()) && debt.getDebtDueDate() != null) {
            debt.setIsOverdue(LocalDateTime.now().isAfter(debt.getDebtDueDate()));
        }

        commonDebtRepository.save(debt);
    }

    @Override
    public void repayCommonDebt(Long debtId, CommonDebtRepaymentRequest request) {
        CommonDebt debt = getCommonDebtDetail(debtId);

        if (!"APPROVED".equals(debt.getApprovalStatus())) {
            throw new BusinessException("债务未审批通过，无法清偿");
        }

        debt.setRepaymentStatus(request.getRepaymentStatus());
        debt.setRepaidAmount(request.getRepaidAmount());
        debt.setRepaymentMethod(request.getRepaymentMethod());
        debt.setRepaymentAccountId(request.getRepaymentAccountId());
        debt.setRepaymentVoucher(request.getRepaymentVoucher());
        debt.setRepaymentDate(request.getRepaymentDate() != null ? request.getRepaymentDate() : LocalDateTime.now());

        if (debt.getDebtAmount() != null) {
            BigDecimal unrepaidAmount = debt.getDebtAmount().subtract(request.getRepaidAmount());
            debt.setUnrepaidAmount(unrepaidAmount.compareTo(BigDecimal.ZERO) > 0 ? unrepaidAmount : BigDecimal.ZERO);
        }

        if ("REPAID".equals(request.getRepaymentStatus())) {
            debt.setIsOverdue(false);
        }

        commonDebtRepository.save(debt);
    }

    @Override
    public void deleteCommonDebt(Long debtId) {
        CommonDebt debt = getCommonDebtDetail(debtId);
        commonDebtRepository.delete(debt);
    }

    private String generateDebtNo() {
        return "CDT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}