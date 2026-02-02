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
        fundFlow.setOperatorId(1L);
        fundFlow.setOperationTime(LocalDateTime.now());
        fundFlow.setStatus("ACTIVE");
        fundFlow.setCreateUserId(SecurityUtil.getCurrentUserId());

        FundFlow saved = fundFlowRepository.save(fundFlow);
        
        // 根据fundAccountId或accountId更新对应资金账户的余额
        Long accountIdToUse = saved.getFundAccountId();
        if (accountIdToUse == null) {
            accountIdToUse = saved.getAccountId();
        }
        if (accountIdToUse != null && saved.getBalanceAfter() != null) {
            FundAccountBalanceRequest balanceRequest = new FundAccountBalanceRequest();
            balanceRequest.setCurrentBalance(saved.getBalanceAfter());
            fundAccountService.updateFundAccountBalance(accountIdToUse, balanceRequest, SecurityUtil.getCurrentUserId());
        }
        
        return saved.getId();
    }

    @Override
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
    public FundFlow getFundFlowDetail(Long flowId) {
        return fundFlowRepository.findById(flowId)
                .orElseThrow(() -> new BusinessException("资金流水不存在"));
    }

    @Override
    public void updateFundFlow(Long flowId, FundFlowUpdateRequest request) {
        FundFlow fundFlow = getFundFlowDetail(flowId);
        BeanUtils.copyProperties(request, fundFlow, "id", "status");
        fundFlow.setUpdateUserId(SecurityUtil.getCurrentUserId());
        
        FundFlow saved = fundFlowRepository.save(fundFlow);
        
        // 根据fundAccountId或accountId更新对应资金账户的余额
        Long accountIdToUse = saved.getFundAccountId();
        if (accountIdToUse == null) {
            accountIdToUse = saved.getAccountId();
        }
        if (accountIdToUse != null && saved.getBalanceAfter() != null) {
            FundAccountBalanceRequest balanceRequest = new FundAccountBalanceRequest();
            balanceRequest.setCurrentBalance(saved.getBalanceAfter());
            fundAccountService.updateFundAccountBalance(accountIdToUse, balanceRequest, SecurityUtil.getCurrentUserId());
        }
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
        fundFlow.setUpdateUserId(SecurityUtil.getCurrentUserId());
        fundFlowRepository.delete(fundFlow);
    }
}
