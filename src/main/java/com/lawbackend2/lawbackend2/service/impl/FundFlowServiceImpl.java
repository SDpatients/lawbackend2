package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundFlowCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundFlow;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundFlowRepository;
import com.lawbackend2.lawbackend2.service.FundFlowService;
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

    public FundFlowServiceImpl(FundFlowRepository fundFlowRepository) {
        this.fundFlowRepository = fundFlowRepository;
    }

    @Override
    public Long createFundFlow(FundFlowCreateRequest request) {
        FundFlow fundFlow = new FundFlow();
        BeanUtils.copyProperties(request, fundFlow);
        fundFlow.setOperatorId(1L);
        fundFlow.setOperationTime(LocalDateTime.now());
        fundFlow.setStatus("ACTIVE");

        FundFlow saved = fundFlowRepository.save(fundFlow);
        return saved.getId();
    }

    @Override
    public PageResult<FundFlow> getFundFlowList(Integer pageNum, Integer pageSize, Long caseId, Long fundAccountId, String flowType, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "transactionDate"));
        Page<FundFlow> page = fundFlowRepository.findByConditions(caseId, fundAccountId, flowType, status, pageable);

        PageResult<FundFlow> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
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
        fundFlowRepository.save(fundFlow);
    }

    @Override
    public void updateFundFlowStatus(Long flowId, FundFlowStatusRequest request) {
        FundFlow fundFlow = getFundFlowDetail(flowId);
        fundFlow.setStatus(request.getStatus());
        fundFlowRepository.save(fundFlow);
    }

    @Override
    public void deleteFundFlow(Long flowId) {
        FundFlow fundFlow = getFundFlowDetail(flowId);
        fundFlowRepository.delete(fundFlow);
    }
}
