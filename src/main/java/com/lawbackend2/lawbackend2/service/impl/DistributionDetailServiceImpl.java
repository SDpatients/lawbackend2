package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.DistributionDetailCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.DistributionDetailPaymentRequest;
import com.lawbackend2.lawbackend2.entity.DistributionDetail;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.DistributionDetailRepository;
import com.lawbackend2.lawbackend2.service.DistributionDetailService;
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
public class DistributionDetailServiceImpl implements DistributionDetailService {

    private final DistributionDetailRepository distributionDetailRepository;

    public DistributionDetailServiceImpl(DistributionDetailRepository distributionDetailRepository) {
        this.distributionDetailRepository = distributionDetailRepository;
    }

    @Override
    public Long createDistributionDetail(DistributionDetailCreateRequest request) {
        DistributionDetail detail = new DistributionDetail();
        BeanUtils.copyProperties(request, detail);
        detail.setPaymentStatus("UNPAID");
        detail.setStatus("ACTIVE");

        if (detail.getAccumulatedDistributionAmount() == null) {
            detail.setAccumulatedDistributionAmount(BigDecimal.ZERO);
        }

        DistributionDetail saved = distributionDetailRepository.save(detail);
        return saved.getId();
    }

    @Override
    public PageResult<DistributionDetail> getDistributionDetailList(Integer pageNum, Integer pageSize, Long distributionExecutionId, Long caseId, String creditorType, String paymentStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<DistributionDetail> page = distributionDetailRepository.findByConditions(distributionExecutionId, caseId, creditorType, paymentStatus, pageable);

        PageResult<DistributionDetail> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public DistributionDetail getDistributionDetailDetail(Long detailId) {
        return distributionDetailRepository.findById(detailId)
                .orElseThrow(() -> new BusinessException("分配明细不存在"));
    }

    @Override
    public void payDistributionDetail(Long detailId, DistributionDetailPaymentRequest request) {
        DistributionDetail detail = getDistributionDetailDetail(detailId);
        detail.setPaymentStatus(request.getPaymentStatus());
        detail.setPaymentMethod(request.getPaymentMethod());
        detail.setPaymentAccountId(request.getPaymentAccountId());
        detail.setPaymentVoucher(request.getPaymentVoucher());
        detail.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDateTime.now());
        distributionDetailRepository.save(detail);
    }

    @Override
    public void deleteDistributionDetail(Long detailId) {
        DistributionDetail detail = getDistributionDetailDetail(detailId);
        distributionDetailRepository.delete(detail);
    }
}