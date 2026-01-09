package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundApprovalRequest;
import com.lawbackend2.lawbackend2.entity.FundApproval;
import com.lawbackend2.lawbackend2.repository.FundApprovalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FundApprovalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FundApprovalRepository fundApprovalRepository;

    private FundApproval testApproval;

    @BeforeEach
    void setUp() {
        testApproval = new FundApproval();
        testApproval.setFlowId(1L);
        testApproval.setCaseId(1L);
        testApproval.setAmount(new BigDecimal("100000.00"));
        testApproval.setApprovalContent("测试审批");
        testApproval.setApprovalStatus("PENDING");
        testApproval.setStatus("ACTIVE");
        testApproval = fundApprovalRepository.save(testApproval);
    }

    @AfterEach
    void tearDown() {
        fundApprovalRepository.deleteAll();
    }

    @Test
    void testCreateFundApproval_Success() throws Exception {
        FundApprovalCreateRequest request = new FundApprovalCreateRequest();
        request.setFlowId(2L);
        request.setCaseId(2L);
        request.setAmount(new BigDecimal("50000.00"));
        request.setApprovalContent("新测试审批");

        mockMvc.perform(post("/fund-approval")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.approvalId").isNumber());
    }

    @Test
    void testGetFundApprovalList_Success() throws Exception {
        mockMvc.perform(get("/fund-approval/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetFundApprovalDetail_Success() throws Exception {
        mockMvc.perform(get("/fund-approval/{approvalId}", testApproval.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testApproval.getId()))
                .andExpect(jsonPath("$.data.approvalStatus").value("PENDING"));
    }

    @Test
    void testApproveFundApproval_Success() throws Exception {
        FundApprovalRequest request = new FundApprovalRequest();
        request.setApprovalStatus("APPROVED");
        request.setApprovalOpinion("审批通过");

        mockMvc.perform(post("/fund-approval/{approvalId}/approve", testApproval.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
