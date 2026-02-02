package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanResponse;
import com.lawbackend2.lawbackend2.entity.WorkPlan;

public interface WorkPlanService {

    Long createWorkPlan(WorkPlanCreateRequest request, Long userId);

    PageResult<WorkPlanResponse> getWorkPlanList(Integer pageNum, Integer pageSize, Long caseId, String planType, String executionStatus, String status, Long userId);

    WorkPlan getWorkPlanDetail(Long planId, Long userId);

    void updateWorkPlan(Long planId, WorkPlanUpdateRequest request, Long userId);

    void updateWorkPlanStatus(Long planId, WorkPlanStatusRequest request, Long userId);

    void deleteWorkPlan(Long planId);

    PageResult<WorkPlanResponse> getWorkPlanListByTimeRange(Integer pageNum, Integer pageSize, String startDate, String endDate, Long userId);
}
