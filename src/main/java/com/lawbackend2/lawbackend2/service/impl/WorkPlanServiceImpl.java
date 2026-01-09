package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanUpdateRequest;
import com.lawbackend2.lawbackend2.entity.WorkPlan;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.WorkPlanRepository;
import com.lawbackend2.lawbackend2.service.WorkPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkPlanServiceImpl implements WorkPlanService {

    private final WorkPlanRepository workPlanRepository;

    public WorkPlanServiceImpl(WorkPlanRepository workPlanRepository) {
        this.workPlanRepository = workPlanRepository;
    }

    @Override
    public Long createWorkPlan(WorkPlanCreateRequest request) {
        WorkPlan workPlan = new WorkPlan();
        BeanUtils.copyProperties(request, workPlan);
        workPlan.setExecutionStatus("NOT_STARTED");
        workPlan.setStatus("ACTIVE");

        WorkPlan saved = workPlanRepository.save(workPlan);
        return saved.getId();
    }

    @Override
    public PageResult<WorkPlan> getWorkPlanList(Integer pageNum, Integer pageSize, Long caseId, String planType, String executionStatus, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<WorkPlan> page = workPlanRepository.findByConditions(caseId, planType, executionStatus, status, pageable);

        PageResult<WorkPlan> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public WorkPlan getWorkPlanDetail(Long planId) {
        return workPlanRepository.findById(planId)
                .orElseThrow(() -> new BusinessException("工作计划不存在"));
    }

    @Override
    public void updateWorkPlan(Long planId, WorkPlanUpdateRequest request) {
        WorkPlan workPlan = getWorkPlanDetail(planId);
        workPlan.setPlanContent(request.getPlanContent());
        workPlan.setStartDate(request.getStartDate());
        workPlan.setEndDate(request.getEndDate());
        workPlan.setResponsibleUserId(request.getResponsibleUserId());
        workPlanRepository.save(workPlan);
    }

    @Override
    public void updateWorkPlanStatus(Long planId, WorkPlanStatusRequest request) {
        WorkPlan workPlan = getWorkPlanDetail(planId);
        workPlan.setExecutionStatus(request.getExecutionStatus());
        workPlanRepository.save(workPlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkPlan(Long planId) {
        if (!workPlanRepository.existsById(planId)) {
            throw new BusinessException("工作计划不存在");
        }
        workPlanRepository.deleteById(planId);
    }
}
