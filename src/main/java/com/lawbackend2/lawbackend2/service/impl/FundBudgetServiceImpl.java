package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundBudgetApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundBudgetCreateRequest;
import com.lawbackend2.lawbackend2.entity.FundBudget;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundBudgetRepository;
import com.lawbackend2.lawbackend2.service.FundBudgetService;
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
public class FundBudgetServiceImpl implements FundBudgetService {

    private final FundBudgetRepository fundBudgetRepository;

    public FundBudgetServiceImpl(FundBudgetRepository fundBudgetRepository) {
        this.fundBudgetRepository = fundBudgetRepository;
    }

    @Override
    public Long createFundBudget(FundBudgetCreateRequest request) {
        FundBudget budget = new FundBudget();
        BeanUtils.copyProperties(request, budget);
        budget.setBudgetNo(generateBudgetNo());
        budget.setApprovalStatus("PENDING");
        budget.setBudgetStatus("ACTIVE");
        budget.setStatus("ACTIVE");

        if (budget.getUsedAmount() == null) {
            budget.setUsedAmount(BigDecimal.ZERO);
        }

        if (budget.getTotalBudgetAmount() != null && budget.getUsedAmount() != null) {
            budget.setRemainingAmount(budget.getTotalBudgetAmount().subtract(budget.getUsedAmount()));
        }

        FundBudget saved = fundBudgetRepository.save(budget);
        return saved.getId();
    }

    @Override
    public PageResult<FundBudget> getFundBudgetList(Integer pageNum, Integer pageSize, Long caseId, String budgetType, String budgetStatus, String approvalStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<FundBudget> page = fundBudgetRepository.findByConditions(caseId, budgetType, budgetStatus, approvalStatus, pageable);

        PageResult<FundBudget> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public FundBudget getFundBudgetDetail(Long budgetId) {
        return fundBudgetRepository.findById(budgetId)
                .orElseThrow(() -> new BusinessException("资金预算不存在"));
    }

    @Override
    public void approveFundBudget(Long budgetId, FundBudgetApprovalRequest request) {
        FundBudget budget = getFundBudgetDetail(budgetId);
        budget.setApprovalStatus(request.getApprovalStatus());
        budget.setApprovalOpinion(request.getApprovalOpinion());
        budget.setApprovalDate(request.getApprovalDate() != null ? request.getApprovalDate() : LocalDateTime.now());

        if ("APPROVED".equals(request.getApprovalStatus())) {
            budget.setBudgetStatus("ACTIVE");
        } else if ("REJECTED".equals(request.getApprovalStatus())) {
            budget.setBudgetStatus("CANCELLED");
        }

        fundBudgetRepository.save(budget);
    }

    @Override
    public void deleteFundBudget(Long budgetId) {
        FundBudget budget = getFundBudgetDetail(budgetId);
        fundBudgetRepository.delete(budget);
    }

    private String generateBudgetNo() {
        return "BGT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}