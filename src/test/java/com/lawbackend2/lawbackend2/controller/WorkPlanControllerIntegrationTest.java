package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.WorkPlanUpdateRequest;
import com.lawbackend2.lawbackend2.entity.WorkPlan;
import com.lawbackend2.lawbackend2.repository.WorkPlanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WorkPlanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkPlanRepository workPlanRepository;

    private WorkPlan testPlan;

    @BeforeEach
    void setUp() {
        testPlan = new WorkPlan();
        testPlan.setPlanNumber("PLAN001");
        testPlan.setPlanType("WEEKLY");
        testPlan.setPlanContent("测试计划内容");
        testPlan.setStartDate(LocalDate.now());
        testPlan.setEndDate(LocalDate.now().plusWeeks(1));
        testPlan.setResponsibleUserId(1L);
        testPlan.setExecutionStatus("NOT_STARTED");
        testPlan.setCaseId(1L);
        testPlan.setStatus("ACTIVE");
        testPlan = workPlanRepository.save(testPlan);
    }

    @AfterEach
    void tearDown() {
        workPlanRepository.deleteAll();
    }

    @Test
    void testCreateWorkPlan_Success() throws Exception {
        WorkPlanCreateRequest request = new WorkPlanCreateRequest();
        request.setPlanNumber("PLAN002");
        request.setPlanType("MONTHLY");
        request.setPlanContent("新测试计划内容");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(1));
        request.setResponsibleUserId(2L);
        request.setCaseId(2L);

        mockMvc.perform(post("/work-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.planId").isNumber());
    }

    @Test
    void testGetWorkPlanList_Success() throws Exception {
        mockMvc.perform(get("/work-plan/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetWorkPlanDetail_Success() throws Exception {
        mockMvc.perform(get("/work-plan/{planId}", testPlan.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testPlan.getId()))
                .andExpect(jsonPath("$.data.planNumber").value("PLAN001"));
    }

    @Test
    void testUpdateWorkPlan_Success() throws Exception {
        WorkPlanUpdateRequest request = new WorkPlanUpdateRequest();
        request.setPlanContent("更新后的计划内容");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusWeeks(2));
        request.setResponsibleUserId(3L);

        mockMvc.perform(put("/work-plan/{planId}", testPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateWorkPlanStatus_Success() throws Exception {
        WorkPlanStatusRequest request = new WorkPlanStatusRequest();
        request.setExecutionStatus("IN_PROGRESS");

        mockMvc.perform(put("/work-plan/{planId}/execution-status", testPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetWorkPlanListByTimeRange_Success() throws Exception {
        // 准备测试数据：在时间区间内的工作计划
        WorkPlan planInRange = new WorkPlan();
        planInRange.setPlanNumber("PLAN002");
        planInRange.setPlanType("DAILY");
        planInRange.setPlanContent("时间区间内的测试计划");
        planInRange.setStartDate(LocalDate.now().minusDays(2));
        planInRange.setEndDate(LocalDate.now().plusDays(2));
        planInRange.setResponsibleUserId(1L);
        planInRange.setExecutionStatus("NOT_STARTED");
        planInRange.setCaseId(1L);
        planInRange.setStatus("ACTIVE");
        workPlanRepository.save(planInRange);

        // 发送请求：查询包含当前日期的时间区间
        String startDate = LocalDate.now().minusDays(1).toString();
        String endDate = LocalDate.now().plusDays(1).toString();

        mockMvc.perform(get("/work-plan/list-by-time")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("startDate", startDate)
                        .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }
}
