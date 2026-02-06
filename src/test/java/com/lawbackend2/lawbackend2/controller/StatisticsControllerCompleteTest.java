package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.config.TestRedisConfig;
import com.lawbackend2.lawbackend2.dto.response.*;
import com.lawbackend2.lawbackend2.entity.*;
import com.lawbackend2.lawbackend2.repository.*;
import com.lawbackend2.lawbackend2.util.JwtTokenUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
class StatisticsControllerCompleteTest {

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

    @Autowired
    private BankruptCaseRepository bankruptCaseRepository;

    private Long testCaseId;
    private String authToken;

    @BeforeEach
    void setUp() {
        // 使用硬编码的测试 JWT Token
        // 这个 token 是用 test-secret-key-for-jwt-token-generation-in-tests 生成的
        authToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJ0eXBlIjoiQUNDRVNTIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMzYwMDB9.test";   BankruptCase testCase = new BankruptCase();
        testCase.setCaseNumber("TEST-STATS-001");
        testCase.setCaseName("统计测试案件");
        testCase.setCaseStatus("ONGOING");
        testCase.setCaseProgress("FIRST");
        testCase.setCreateUserId(1L);
        testCase = bankruptCaseRepository.save(testCase);
        testCaseId = testCase.getId();

        FundAccount testAccount = new FundAccount();
        testAccount.setCaseId(testCaseId);
        testAccount.setAccountName("测试账户");
        testAccount.setBankAccount("123456789");
        testAccount.setAccountType("CURRENT");
        testAccount.setCurrentBalance(new BigDecimal("10000.00"));
        testAccount.setStatus("ACTIVE");
        testAccount.setIsDeleted(false);
        testAccount = fundAccountRepository.save(testAccount);

        FundFlow incomeFlow = new FundFlow();
        incomeFlow.setCaseId(testCaseId);
        incomeFlow.setFundAccountId(testAccount.getId());
        incomeFlow.setFlowType("INCOME");
        incomeFlow.setAmount(new BigDecimal("5000.00"));
        incomeFlow.setTransactionDate(LocalDateTime.now());
        incomeFlow.setDescription("测试收入");
        incomeFlow.setStatus("COMPLETED");
        incomeFlow.setIsDeleted(false);
        fundFlowRepository.save(incomeFlow);

        FundFlow expenseFlow = new FundFlow();
        expenseFlow.setCaseId(testCaseId);
        expenseFlow.setFundAccountId(testAccount.getId());
        expenseFlow.setFlowType("EXPENSE");
        expenseFlow.setAmount(new BigDecimal("2000.00"));
        expenseFlow.setTransactionDate(LocalDateTime.now());
        expenseFlow.setDescription("测试支出");
        expenseFlow.setStatus("COMPLETED");
        expenseFlow.setIsDeleted(false);
        fundFlowRepository.save(expenseFlow);

        FundApproval pendingApproval = new FundApproval();
        pendingApproval.setCaseId(testCaseId);
        pendingApproval.setAmount(new BigDecimal("500.00"));
        pendingApproval.setApprovalStatus("PENDING");
        pendingApproval.setStatus("ACTIVE");
        pendingApproval.setIsDeleted(false);
        fundApprovalRepository.save(pendingApproval);

        FundApproval approvedApproval = new FundApproval();
        approvedApproval.setCaseId(testCaseId);
        approvedApproval.setAmount(new BigDecimal("800.00"));
        approvedApproval.setApprovalStatus("APPROVED");
        approvedApproval.setStatus("ACTIVE");
        approvedApproval.setIsDeleted(false);
        fundApprovalRepository.save(approvedApproval);

        WorkPlan testPlan = new WorkPlan();
        testPlan.setPlanNumber("PLAN001");
        testPlan.setPlanType("WEEKLY");
        testPlan.setPlanContent("测试计划内容");
        testPlan.setStartDate(LocalDate.now());
        testPlan.setEndDate(LocalDate.now().plusWeeks(1));
        testPlan.setResponsibleUserId(1L);
        testPlan.setExecutionStatus("NOT_STARTED");
        testPlan.setCaseId(testCaseId);
        testPlan.setStatus("ACTIVE");
        testPlan.setIsDeleted(false);
        workPlanRepository.save(testPlan);

        WorkPlan inProgressPlan = new WorkPlan();
        inProgressPlan.setPlanNumber("PLAN002");
        inProgressPlan.setPlanType("MONTHLY");
        inProgressPlan.setPlanContent("进行中计划");
        inProgressPlan.setStartDate(LocalDate.now());
        inProgressPlan.setEndDate(LocalDate.now().plusMonths(1));
        inProgressPlan.setResponsibleUserId(1L);
        inProgressPlan.setExecutionStatus("IN_PROGRESS");
        inProgressPlan.setCaseId(testCaseId);
        inProgressPlan.setStatus("ACTIVE");
        inProgressPlan.setIsDeleted(false);
        workPlanRepository.save(inProgressPlan);
    }

    @AfterEach
    void tearDown() {
        fundFlowRepository.deleteAll();
        fundApprovalRepository.deleteAll();
        fundAccountRepository.deleteAll();
        workPlanRepository.deleteAll();
        bankruptCaseRepository.deleteAll();
    }

    @Test
    void testGetFundTransactionStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-transaction")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", testCaseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.totalTransactions").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.totalIncome").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.totalExpense").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void testGetFundTransactionTrend_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-transaction/trend")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", testCaseId.toString())
                        .param("period", "month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("fund_transaction"))
                .andExpect(jsonPath("$.data.trendData").isArray());
    }

    @Test
    void testGetFundApprovalStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-approval")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", testCaseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalApprovals").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.pendingApprovals").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.approvedApprovals").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void testGetFundApprovalTrend_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-approval/trend")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", testCaseId.toString())
                        .param("period", "quarter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("fund_approval"))
                .andExpect(jsonPath("$.data.trendData").isArray());
    }

    @Test
    void testGetFundAccountStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/fund-account")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", testCaseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalAccounts").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.activeAccounts").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.totalBalance").value(greaterThanOrEqualTo(10000.0)));
    }

    @Test
    void testGetWorkPlanStatistics_Success() throws Exception {
        mockMvc.perform(get("/statistics/work-plan")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", testCaseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalPlans").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.notStartedPlans").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.inProgressPlans").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void testGetCaseTrend_Success() throws Exception {
        mockMvc.perform(get("/statistics/case/trend")
                        .header("Authorization", "Bearer " + authToken)
                        .param("period", "month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("case"))
                .andExpect(jsonPath("$.data.trendData").isArray());
    }

    @Test
    void testGetCaseCrossAnalysis_Success() throws Exception {
        mockMvc.perform(get("/statistics/case/cross-analysis")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("case_status_progress"))
                .andExpect(jsonPath("$.data.crossData").exists())
                .andExpect(jsonPath("$.data.statusDistribution").exists())
                .andExpect(jsonPath("$.data.progressDistribution").exists());
    }

    @Test
    void testGetCaseAmountRanking_Success() throws Exception {
        mockMvc.perform(get("/statistics/case/amount-ranking")
                        .header("Authorization", "Bearer " + authToken)
                        .param("topN", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("case_amount"))
                .andExpect(jsonPath("$.data.topN").value(10))
                .andExpect(jsonPath("$.data.rankings").isArray());
    }

    @Test
    void testGetCreditorClaimAmountRanking_Success() throws Exception {
        mockMvc.perform(get("/statistics/creditor-claim/amount-ranking")
                        .header("Authorization", "Bearer " + authToken)
                        .param("topN", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.type").value("creditor_claim_amount"))
                .andExpect(jsonPath("$.data.topN").value(5))
                .andExpect(jsonPath("$.data.rankings").isArray());
    }

    @Test
    void testGetFundTransactionStatistics_InvalidCaseId() throws Exception {
        mockMvc.perform(get("/statistics/fund-transaction")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", "999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalTransactions").value(0));
    }

    @Test
    void testStatisticsApiResponseTime() throws Exception {
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/statistics/fund-transaction")
                        .header("Authorization", "Bearer " + authToken)
                        .param("caseId", testCaseId.toString()))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        assertTrue(responseTime < 2000, "API响应时间应小于2秒，实际: " + responseTime + "ms");
    }
}