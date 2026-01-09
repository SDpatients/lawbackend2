package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundApproval;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundApprovalRepository;
import com.lawbackend2.lawbackend2.service.FundApprovalService;
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
public class FundApprovalServiceImpl implements FundApprovalService {

    private final FundApprovalRepository fundApprovalRepository;

    public FundApprovalServiceImpl(FundApprovalRepository fundApprovalRepository) {
        this.fundApprovalRepository = fundApprovalRepository;
    }

    @Override
    public Long createFundApproval(FundApprovalCreateRequest request) {
        FundApproval fundApproval = new FundApproval();
        BeanUtils.copyProperties(request, fundApproval);
        fundApproval.setApprovalStatus("PENDING");
        fundApproval.setStatus("ACTIVE");

        FundApproval saved = fundApprovalRepository.save(fundApproval);
        return saved.getId();
    }

    @Override
    public PageResult<FundApproval> getFundApprovalList(Integer pageNum, Integer pageSize, Long caseId, String approvalStatus, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<FundApproval> page = fundApprovalRepository.findByConditions(caseId, approvalStatus, status, pageable);

        PageResult<FundApproval> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public FundApproval getFundApprovalDetail(Long approvalId) {
        return fundApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("资金审批不存在"));
    }

    @Override
    public void updateFundApproval(Long approvalId, FundApprovalUpdateRequest request) {
        FundApproval fundApproval = getFundApprovalDetail(approvalId);
        BeanUtils.copyProperties(request, fundApproval, "id", "approvalStatus", "approverId", "approvalTime", "approvalOpinion", "status");
        fundApprovalRepository.save(fundApproval);
    }

    @Override
    public void approveFundApproval(Long approvalId, FundApprovalRequest request) {
        FundApproval fundApproval = getFundApprovalDetail(approvalId);

        if (!"PENDING".equals(fundApproval.getApprovalStatus())) {
            throw new BusinessException("该审批已处理，无法重复审批");
        }

        fundApproval.setApprovalStatus(request.getApprovalStatus());
        fundApproval.setApprovalOpinion(request.getApprovalOpinion());
        fundApproval.setApproverId(1L);
        fundApproval.setApprovalTime(LocalDateTime.now());

        fundApprovalRepository.save(fundApproval);
    }

    @Override
    public void updateFundApprovalStatus(Long approvalId, FundApprovalStatusRequest request) {
        FundApproval fundApproval = getFundApprovalDetail(approvalId);
        fundApproval.setStatus(request.getStatus());
        fundApprovalRepository.save(fundApproval);
    }

    @Override
    public void deleteFundApproval(Long approvalId) {
        FundApproval fundApproval = getFundApprovalDetail(approvalId);
        fundApprovalRepository.delete(fundApproval);
    }
}
