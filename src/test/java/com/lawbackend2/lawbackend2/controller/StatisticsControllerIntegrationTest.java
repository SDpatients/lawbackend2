package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.response.FundAccountStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundApprovalStatistics;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionExport;
import com.lawbackend2.lawbackend2.dto.response.FundTransactionStatistics;
import com.lawbackend2.lawbackend2.dto.response.WorkPlanStatistics;
import com.lawbackend2.lawbackend2.entity.FundAccount;
import com.lawbackend2.lawbackend2.entity.FundApproval;
import com.lawbackend2.lawbackend2.entity.FundFlow;
import com.lawbackend2.lawbackend2.entity.WorkPlan;
import com.lawbackend2.lawbackend2.repository.FundAccountRepository;
import com.lawbackend2.lawbackend2.repository.FundApprovalRepository;
import com.lawbackend2.lawbackend2.repository.FundFlowRepository;
import com.lawbackend2.lawbackend2.repository.WorkPlanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StatisticsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FundFlowRepository fundFlowRepository;

    @Autowired
    private FundApprovalRepository fundApprovalRepository;

    @Autowired
    private FundAccountRepository fundAccountRepository;

    @Autowired
    private WorkPlanRepository workPlanRepository;

    private FundFlow testTransaction;
    private FundApproval testApproval;
    private FundAccount testAccount;
    private WorkPlan testPlan;

    @BeforeEach
    void setUp() {
        testAccount = new FundAccount();
        testAccount.setCaseId(1L);
        testAccount.setAccountName("测试账户");
        testAccount.setBankAccount("123456789");
        testAccount.setAccountType("CURRENT");
        testAccount.setCurrentBalance(new BigDecimal("10000.00"));
        testAccount.setStatus("ACTIVE");
        testAccount = fundAccountRepository.save(testAccount);

        testTransaction = new FundFlow();
        testTransaction.setCaseId(1L);
        testTransaction.setFundAccountId(testAccount.getId());
        testTransaction.setFlowType("INCOME");
        testTransaction.setAmount(new BigDecimal("1000.00"));
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setDescription("测试交易");
        testTransaction.setStatus("COMPLETED");
        testTransaction = fundFlowRepository.save(testTransaction);

        testApproval = new FundApproval();
        testApproval.setCaseId(1L);
        testApproval.setAmount(new BigDecimal("500.00"));
        testApproval.setApprovalStatus("PENDING");
        testApproval.setStatus("ACTIVE");
        testApproval = fundApprovalRepository.save(testApproval);

        testPlan = new WorkPlan();
        testPlan.setPlanNumber("PLAN001");
        testPlan.setPlanType("WEEKLY");
        testPlan.setPlanContent("测试计划");
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
        fundFlowRepository.deleteAll();
        fundApprovalRepository.deleteAll();
        fundAccountRepository.deleteAll();
        workPlanRepository.deleteAll();
    }

    @Test
    void testGetFundTransactionStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-transaction")
                        .param("caseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalTransactions").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.totalIncome").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.totalExpense").value(greaterThanOrEqualTo(0)));
    }

    @Test
    void testExportFundTransactions_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-transaction/export")
                        .param("caseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileName").isString())
                .andExpect(jsonPath("$.data.totalCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.transactions").isArray());
    }

    @Test
    void testGetFundApprovalStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-approval")
                        .param("caseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalApprovals").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.pendingApprovals").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.approvedApprovals").value(greaterThanOrEqualTo(0)));
    }

    @Test
    void testGetFundAccountStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-account")
                        .param("caseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalAccounts").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.activeAccounts").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.inactiveAccounts").value(greaterThanOrEqualTo(0)));
    }

    @Test
    void testGetWorkPlanStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/work-plan")
                        .param("caseId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalPlans").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.notStartedPlans").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.inProgressPlans").value(greaterThanOrEqualTo(0)));
    }
}
