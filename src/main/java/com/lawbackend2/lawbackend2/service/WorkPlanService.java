package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanUpdateRequest;
import com.lawbackend2.lawbackend2.entity.WorkPlan;

public interface WorkPlanService {

    Long createWorkPlan(WorkPlanCreateRequest request);

    PageResult<WorkPlan> getWorkPlanList(Integer pageNum, Integer pageSize, Long caseId, String planType, String executionStatus, String status);

    WorkPlan getWorkPlanDetail(Long planId);

    void updateWorkPlan(Long planId, WorkPlanUpdateRequest request);

    void updateWorkPlanStatus(Long planId, WorkPlanStatusRequest request);

    void deleteWorkPlan(Long planId);
}
