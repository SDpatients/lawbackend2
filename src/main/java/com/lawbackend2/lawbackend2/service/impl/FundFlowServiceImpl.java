package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.FundAccountBalanceRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundAccount;
import com.lawbackend2.lawbackend2.entity.FundFlow;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundFlowRepository;
import com.lawbackend2.lawbackend2.service.FundAccountService;
import com.lawbackend2.lawbackend2.service.FundFlowService;
import com.lawbackend2.lawbackend2.service.UserService;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.response.FundFlowResponse;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class FundFlowServiceImpl implements FundFlowService {

    private final FundFlowRepository fundFlowRepository;
    private final FundAccountService fundAccountService;
    private final UserService userService;

    public FundFlowServiceImpl(FundFlowRepository fundFlowRepository, FundAccountService fundAccountService, UserService userService) {
        this.fundFlowRepository = fundFlowRepository;
        this.fundAccountService = fundAccountService;
        this.userService = userService;
    }

    @Override
    public Long createFundFlow(FundFlowCreateRequest request) {
        FundFlow fundFlow = new FundFlow();
        BeanUtils.copyProperties(request, fundFlow);
        fundFlow.setOperatorId(SecurityUtil.getCurrentUserId());
        fundFlow.setOperationTime(LocalDateTime.now());
        fundFlow.setStatus("ACTIVE");
        fundFlow.setCreateUserId(SecurityUtil.getCurrentUserId());

        Long fundAccountId = fundFlow.getFundAccountId();
        if (fundAccountId != null) {
            FundAccount account = fundAccountService.getFundAccountDetail(fundAccountId);
            BigDecimal currentBalance = account.getCurrentBalance() != null ? account.getCurrentBalance() : BigDecimal.ZERO;

            if (fundFlow.getBalanceBefore() == null) {
                fundFlow.setBalanceBefore(currentBalance);
            }

            if (fundFlow.getAmount() != null) {
                BigDecimal newBalance;
                String flowType = fundFlow.getFlowType();
                if ("INCOME".equalsIgnoreCase(flowType) || "收入".equals(flowType)) {
                    newBalance = fundFlow.getBalanceBefore().add(fundFlow.getAmount());
                } else if ("EXPENSE".equalsIgnoreCase(flowType) || "支出".equals(flowType)) {
                    newBalance = fundFlow.getBalanceBefore().subtract(fundFlow.getAmount());
                } else {
                    newBalance = fundFlow.getBalanceAfter() != null ? fundFlow.getBalanceAfter() : currentBalance;
                }
                fundFlow.setBalanceAfter(newBalance);
            }
        }

        FundFlow saved = fundFlowRepository.save(fundFlow);

        if (fundAccountId != null && saved.getBalanceAfter() != null) {
            FundAccountBalanceRequest balanceRequest = new FundAccountBalanceRequest();
            balanceRequest.setCurrentBalance(saved.getBalanceAfter());
            fundAccountService.updateFundAccountBalance(fundAccountId, balanceRequest, SecurityUtil.getCurrentUserId());
        }

        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<FundFlowResponse> getFundFlowList(Integer pageNum, Integer pageSize, Long caseId, Long fundAccountId, String flowType, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Page<FundFlow> page = fundFlowRepository.findByConditions(caseId, fundAccountId, flowType, status, pageable);

        List<FundFlowResponse> fundFlowResponses = page.getContent().stream()
                .map(fundFlow -> {
                    FundFlowResponse response = new FundFlowResponse();
                    BeanUtils.copyProperties(fundFlow, response);
                    if (fundFlow.getOperatorId() != null) {
                        try {
                            response.setOperatorRealName(userService.getUserById(fundFlow.getOperatorId()).getRealName());
                        } catch (Exception e) {
                            response.setOperatorRealName("未知用户");
                        }
                    } else {
                        response.setOperatorRealName("系统操作");
                    }
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());

        PageResult<FundFlowResponse> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(fundFlowResponses);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public FundFlow getFundFlowDetail(Long flowId) {
        return fundFlowRepository.findById(flowId)
                .orElseThrow(() -> new BusinessException("资金流水不存在"));
    }

    @Override
    public void updateFundFlow(Long flowId, FundFlowUpdateRequest request) {
        FundFlow fundFlow = getFundFlowDetail(flowId);

        Long oldFundAccountId = fundFlow.getFundAccountId();
        BigDecimal oldAmount = fundFlow.getAmount();
        String oldFlowType = fundFlow.getFlowType();

        BeanUtils.copyProperties(request, fundFlow, "id", "status");
        fundFlow.setUpdateUserId(SecurityUtil.getCurrentUserId());

        Long newFundAccountId = fundFlow.getFundAccountId();

        if (oldFundAccountId != null && oldAmount != null) {
            try {
                FundAccount oldAccount = fundAccountService.getFundAccountDetail(oldFundAccountId);
                BigDecimal oldAccountBalance = oldAccount.getCurrentBalance() != null ? oldAccount.getCurrentBalance() : BigDecimal.ZERO;
                BigDecimal reverseAmount;
                if ("INCOME".equalsIgnoreCase(oldFlowType) || "收入".equals(oldFlowType)) {
                    reverseAmount = oldAccountBalance.subtract(oldAmount);
                } else if ("EXPENSE".equalsIgnoreCase(oldFlowType) || "支出".equals(oldFlowType)) {
                    reverseAmount = oldAccountBalance.add(oldAmount);
                } else {
                    reverseAmount = oldAccountBalance;
                }
                FundAccountBalanceRequest reverseRequest = new FundAccountBalanceRequest();
                reverseRequest.setCurrentBalance(reverseAmount);
                fundAccountService.updateFundAccountBalance(oldFundAccountId, reverseRequest, SecurityUtil.getCurrentUserId());
            } catch (Exception e) {
                // ignore
            }
        }

        if (newFundAccountId != null && fundFlow.getAmount() != null) {
            FundAccount newAccount = fundAccountService.getFundAccountDetail(newFundAccountId);
            BigDecimal newAccountBalance = newAccount.getCurrentBalance() != null ? newAccount.getCurrentBalance() : BigDecimal.ZERO;

            if (oldFundAccountId == null) {
                fundFlow.setBalanceBefore(newAccountBalance);
            }

            BigDecimal newBalance;
            String newFlowType = fundFlow.getFlowType();
            if ("INCOME".equalsIgnoreCase(newFlowType) || "收入".equals(newFlowType)) {
                newBalance = newAccountBalance.add(fundFlow.getAmount());
            } else if ("EXPENSE".equalsIgnoreCase(newFlowType) || "支出".equals(newFlowType)) {
                newBalance = newAccountBalance.subtract(fundFlow.getAmount());
            } else {
                newBalance = fundFlow.getBalanceAfter() != null ? fundFlow.getBalanceAfter() : newAccountBalance;
            }
            fundFlow.setBalanceAfter(newBalance);

            FundAccountBalanceRequest applyRequest = new FundAccountBalanceRequest();
            applyRequest.setCurrentBalance(newBalance);
            fundAccountService.updateFundAccountBalance(newFundAccountId, applyRequest, SecurityUtil.getCurrentUserId());
        }

        fundFlowRepository.save(fundFlow);
    }

    @Override
    public void updateFundFlowStatus(Long flowId, FundFlowStatusRequest request) {
        FundFlow fundFlow = getFundFlowDetail(flowId);
        fundFlow.setStatus(request.getStatus());
        fundFlow.setUpdateUserId(SecurityUtil.getCurrentUserId());
        fundFlowRepository.save(fundFlow);
    }

    @Override
    public void deleteFundFlow(Long flowId) {
        FundFlow fundFlow = getFundFlowDetail(flowId);

        Long fundAccountId = fundFlow.getFundAccountId();
        if (fundAccountId != null && fundFlow.getAmount() != null) {
            try {
                FundAccount account = fundAccountService.getFundAccountDetail(fundAccountId);
                BigDecimal currentBalance = account.getCurrentBalance() != null ? account.getCurrentBalance() : BigDecimal.ZERO;
                BigDecimal reversedBalance;
                String flowType = fundFlow.getFlowType();
                if ("INCOME".equalsIgnoreCase(flowType) || "收入".equals(flowType)) {
                    reversedBalance = currentBalance.subtract(fundFlow.getAmount());
                } else if ("EXPENSE".equalsIgnoreCase(flowType) || "支出".equals(flowType)) {
                    reversedBalance = currentBalance.add(fundFlow.getAmount());
                } else {
                    reversedBalance = currentBalance;
                }
                FundAccountBalanceRequest balanceRequest = new FundAccountBalanceRequest();
                balanceRequest.setCurrentBalance(reversedBalance);
                fundAccountService.updateFundAccountBalance(fundAccountId, balanceRequest, SecurityUtil.getCurrentUserId());
            } catch (Exception e) {
                // ignore
            }
        }

        fundFlow.setUpdateUserId(SecurityUtil.getCurrentUserId());
        fundFlowRepository.delete(fundFlow);
    }
}
