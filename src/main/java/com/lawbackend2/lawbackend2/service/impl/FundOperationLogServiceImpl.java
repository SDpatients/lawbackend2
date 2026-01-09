package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundOperationLogUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundOperationLog;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundOperationLogRepository;
import com.lawbackend2.lawbackend2.service.FundOperationLogService;
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
public class FundOperationLogServiceImpl implements FundOperationLogService {

    private final FundOperationLogRepository fundOperationLogRepository;

    public FundOperationLogServiceImpl(FundOperationLogRepository fundOperationLogRepository) {
        this.fundOperationLogRepository = fundOperationLogRepository;
    }

    @Override
    public Long createFundOperationLog(FundOperationLogCreateRequest request) {
        FundOperationLog fundOperationLog = new FundOperationLog();
        BeanUtils.copyProperties(request, fundOperationLog);
        fundOperationLog.setOperationTime(LocalDateTime.now());
        fundOperationLog.setStatus("ACTIVE");

        FundOperationLog saved = fundOperationLogRepository.save(fundOperationLog);
        return saved.getId();
    }

    @Override
    public PageResult<FundOperationLog> getFundOperationLogList(Integer pageNum, Integer pageSize, Long caseId, String operationType, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "operationTime"));
        Page<FundOperationLog> page = fundOperationLogRepository.findByConditions(caseId, operationType, status, pageable);

        PageResult<FundOperationLog> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public FundOperationLog getFundOperationLogDetail(Long logId) {
        return fundOperationLogRepository.findById(logId)
                .orElseThrow(() -> new BusinessException("资金操作日志不存在"));
    }

    @Override
    public void updateFundOperationLog(Long logId, FundOperationLogUpdateRequest request) {
        FundOperationLog fundOperationLog = getFundOperationLogDetail(logId);
        BeanUtils.copyProperties(request, fundOperationLog, "id", "status");
        fundOperationLogRepository.save(fundOperationLog);
    }

    @Override
    public void updateFundOperationLogStatus(Long logId, FundOperationLogStatusRequest request) {
        FundOperationLog fundOperationLog = getFundOperationLogDetail(logId);
        fundOperationLog.setStatus(request.getStatus());
        fundOperationLogRepository.save(fundOperationLog);
    }

    @Override
    public void deleteFundOperationLog(Long logId) {
        FundOperationLog fundOperationLog = getFundOperationLogDetail(logId);
        fundOperationLogRepository.delete(fundOperationLog);
    }
}
