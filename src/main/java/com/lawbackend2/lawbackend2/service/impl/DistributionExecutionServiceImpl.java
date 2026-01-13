package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionCreateRequest;
import com.lawbackend2.lawbackend2.entity.DistributionExecution;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.DistributionExecutionRepository;
import com.lawbackend2.lawbackend2.service.DistributionExecutionService;
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
public class DistributionExecutionServiceImpl implements DistributionExecutionService {

    private final DistributionExecutionRepository distributionExecutionRepository;

    public DistributionExecutionServiceImpl(DistributionExecutionRepository distributionExecutionRepository) {
        this.distributionExecutionRepository = distributionExecutionRepository;
    }

    @Override
    public Long createDistributionExecution(DistributionExecutionCreateRequest request) {
        DistributionExecution execution = new DistributionExecution();
        BeanUtils.copyProperties(request, execution);
        execution.setDistributionNo(generateDistributionNo());
        execution.setApprovalStatus("PENDING");
        execution.setExecutionStatus("PENDING");
        execution.setStatus("ACTIVE");

        if (execution.getAccumulatedDistributionAmount() == null) {
            execution.setAccumulatedDistributionAmount(BigDecimal.ZERO);
        }

        DistributionExecution saved = distributionExecutionRepository.save(execution);
        return saved.getId();
    }

    @Override
    public PageResult<DistributionExecution> getDistributionExecutionList(Integer pageNum, Integer pageSize, Long caseId, String distributionBatch, String approvalStatus, String executionStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<DistributionExecution> page = distributionExecutionRepository.findByConditions(caseId, distributionBatch, approvalStatus, executionStatus, pageable);

        PageResult<DistributionExecution> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public DistributionExecution getDistributionExecutionDetail(Long executionId) {
        return distributionExecutionRepository.findById(executionId)
                .orElseThrow(() -> new BusinessException("分配执行不存在"));
    }

    @Override
    public void approveDistributionExecution(Long executionId, DistributionExecutionApprovalRequest request) {
        DistributionExecution execution = getDistributionExecutionDetail(executionId);
        execution.setApprovalStatus(request.getApprovalStatus());
        execution.setApprovalOpinion(request.getApprovalOpinion());
        execution.setApprovalDate(request.getApprovalDate() != null ? request.getApprovalDate() : LocalDateTime.now());
        distributionExecutionRepository.save(execution);
    }

    @Override
    public void executeDistribution(Long executionId) {
        DistributionExecution execution = getDistributionExecutionDetail(executionId);

        if (!"APPROVED".equals(execution.getApprovalStatus())) {
            throw new BusinessException("分配执行未审批通过，无法执行");
        }

        execution.setExecutionStatus("EXECUTING");
        execution.setDistributionDate(LocalDateTime.now());
        distributionExecutionRepository.save(execution);
    }

    @Override
    public void deleteDistributionExecution(Long executionId) {
        DistributionExecution execution = getDistributionExecutionDetail(executionId);
        distributionExecutionRepository.delete(execution);
    }

    private String generateDistributionNo() {
        return "DST" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}