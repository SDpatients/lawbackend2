package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.FundFlowCreateRequest;
import com.lawbackend2.lawbackend2.entity.FundFlow;
import com.lawbackend2.lawbackend2.repository.FundFlowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FundFlowControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FundFlowRepository fundFlowRepository;

    private FundFlow testFundFlow;

    @BeforeEach
    void setUp() {
        testFundFlow = new FundFlow();
        testFundFlow.setCaseId(1L);
        testFundFlow.setCaseName("测试案件");
        testFundFlow.setFundAccountId(1L);
        testFundFlow.setFlowType("INCOME");
        testFundFlow.setAmount(new BigDecimal("100000.00"));
        testFundFlow.setBalanceBefore(new BigDecimal("1000000.00"));
        testFundFlow.setBalanceAfter(new BigDecimal("1100000.00"));
        testFundFlow.setTransactionDate(LocalDateTime.now());
        testFundFlow.setDescription("测试收入");
        testFundFlow.setOperatorId(1L);
        testFundFlow.setOperationTime(LocalDateTime.now());
        testFundFlow.setStatus("ACTIVE");
        testFundFlow = fundFlowRepository.save(testFundFlow);
    }

    @AfterEach
    void tearDown() {
        fundFlowRepository.deleteAll();
    }

    @Test
    void testCreateFundFlow_Success() throws Exception {
        FundFlowCreateRequest request = new FundFlowCreateRequest();
        request.setCaseId(2L);
        request.setCaseName("新测试案件");
        request.setFundAccountId(2L);
        request.setFlowType("EXPENSE");
        request.setAmount(new BigDecimal("50000.00"));
        request.setBalanceBefore(new BigDecimal("2000000.00"));
        request.setBalanceAfter(new BigDecimal("1950000.00"));
        request.setTransactionDate(LocalDateTime.now());
        request.setDescription("测试支出");

        mockMvc.perform(post("/fund-flow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.flowId").isNumber());
    }

    @Test
    void testGetFundFlowList_Success() throws Exception {
        mockMvc.perform(get("/fund-flow/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetFundFlowDetail_Success() throws Exception {
        mockMvc.perform(get("/fund-flow/{flowId}", testFundFlow.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testFundFlow.getId()))
                .andExpect(jsonPath("$.data.flowType").value("INCOME"));
    }
}
