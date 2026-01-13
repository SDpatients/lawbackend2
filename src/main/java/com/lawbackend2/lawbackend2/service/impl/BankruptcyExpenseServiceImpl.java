package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpensePaymentRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptcyExpense;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptcyExpenseRepository;
import com.lawbackend2.lawbackend2.service.BankruptcyExpenseService;
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
public class BankruptcyExpenseServiceImpl implements BankruptcyExpenseService {

    private final BankruptcyExpenseRepository bankruptcyExpenseRepository;

    public BankruptcyExpenseServiceImpl(BankruptcyExpenseRepository bankruptcyExpenseRepository) {
        this.bankruptcyExpenseRepository = bankruptcyExpenseRepository;
    }

    @Override
    public Long createBankruptcyExpense(BankruptcyExpenseCreateRequest request) {
        BankruptcyExpense expense = new BankruptcyExpense();
        BeanUtils.copyProperties(request, expense);
        expense.setExpenseNo(generateExpenseNo());
        expense.setApprovalStatus("PENDING");
        expense.setPaymentStatus("UNPAID");
        expense.setStatus("ACTIVE");

        BankruptcyExpense saved = bankruptcyExpenseRepository.save(expense);
        return saved.getId();
    }

    @Override
    public PageResult<BankruptcyExpense> getBankruptcyExpenseList(Integer pageNum, Integer pageSize, Long caseId, String expenseType, String approvalStatus, String paymentStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BankruptcyExpense> page = bankruptcyExpenseRepository.findByConditions(caseId, expenseType, approvalStatus, paymentStatus, pageable);

        PageResult<BankruptcyExpense> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public BankruptcyExpense getBankruptcyExpenseDetail(Long expenseId) {
        return bankruptcyExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new BusinessException("破产费用不存在"));
    }

    @Override
    public void updateBankruptcyExpense(Long expenseId, BankruptcyExpenseUpdateRequest request) {
        BankruptcyExpense expense = getBankruptcyExpenseDetail(expenseId);
        BeanUtils.copyProperties(request, expense, "id", "expenseNo", "caseId", "caseName", "approvalStatus", "paymentStatus");
        bankruptcyExpenseRepository.save(expense);
    }

    @Override
    public void approveBankruptcyExpense(Long expenseId, BankruptcyExpenseApprovalRequest request) {
        BankruptcyExpense expense = getBankruptcyExpenseDetail(expenseId);
        expense.setApprovalStatus(request.getApprovalStatus());
        expense.setApprovalOpinion(request.getApprovalOpinion());
        expense.setApprovalDate(LocalDateTime.now());

        if (request.getApprovedAmount() != null) {
            expense.setApprovedAmount(request.getApprovedAmount());
        }

        if ("APPROVED".equals(request.getApprovalStatus())) {
            BigDecimal unpaidAmount = expense.getApprovedAmount().subtract(expense.getPaidAmount() != null ? expense.getPaidAmount() : BigDecimal.ZERO);
            expense.setUnpaidAmount(unpaidAmount);
        }

        bankruptcyExpenseRepository.save(expense);
    }

    @Override
    public void payBankruptcyExpense(Long expenseId, BankruptcyExpensePaymentRequest request) {
        BankruptcyExpense expense = getBankruptcyExpenseDetail(expenseId);

        if (!"APPROVED".equals(expense.getApprovalStatus())) {
            throw new BusinessException("费用未审批通过，无法支付");
        }

        expense.setPaymentStatus(request.getPaymentStatus());
        expense.setPaidAmount(request.getPaidAmount());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setPaymentAccountId(request.getPaymentAccountId());
        expense.setPaymentVoucher(request.getPaymentVoucher());
        expense.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());

        if (expense.getApprovedAmount() != null) {
            BigDecimal unpaidAmount = expense.getApprovedAmount().subtract(request.getPaidAmount());
            expense.setUnpaidAmount(unpaidAmount.compareTo(BigDecimal.ZERO) > 0 ? unpaidAmount : BigDecimal.ZERO);
        }

        bankruptcyExpenseRepository.save(expense);
    }

    @Override
    public void deleteBankruptcyExpense(Long expenseId) {
        BankruptcyExpense expense = getBankruptcyExpenseDetail(expenseId);
        bankruptcyExpenseRepository.delete(expense);
    }

    private String generateExpenseNo() {
        return "EXP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}