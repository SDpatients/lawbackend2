package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.FundAccountBalanceRequest;
import com.lawbackend2.lawbackend2.dto.request.FundAccountCreateRequest;
import com.lawbackend2.lawbackend2.entity.FundAccount;
import com.lawbackend2.lawbackend2.repository.FundAccountRepository;
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
class FundAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FundAccountRepository fundAccountRepository;

    private FundAccount testFundAccount;

    @BeforeEach
    void setUp() {
        testFundAccount = new FundAccount();
        testFundAccount.setCaseId(1L);
        testFundAccount.setCaseName("测试案件");
        testFundAccount.setAccountName("测试资金账户");
        testFundAccount.setAccountType("基本户");
        testFundAccount.setInitialBalance(new BigDecimal("1000000.00"));
        testFundAccount.setCurrentBalance(new BigDecimal("1000000.00"));
        testFundAccount.setBankName("中国银行");
        testFundAccount.setBankAccount("1234567890");
        testFundAccount.setStatus("ACTIVE");
        testFundAccount = fundAccountRepository.save(testFundAccount);
    }

    @AfterEach
    void tearDown() {
        fundAccountRepository.deleteAll();
    }

    @Test
    void testCreateFundAccount_Success() throws Exception {
        FundAccountCreateRequest request = new FundAccountCreateRequest();
        request.setCaseId(2L);
        request.setCaseName("新测试案件");
        request.setAccountName("新测试资金账户");
        request.setAccountType("一般户");
        request.setInitialBalance(new BigDecimal("2000000.00"));
        request.setBankName("工商银行");
        request.setBankAccount("9876543210");

        mockMvc.perform(post("/fund-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fundAccountId").isNumber());
    }

    @Test
    void testGetFundAccountList_Success() throws Exception {
        mockMvc.perform(get("/fund-account/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetFundAccountDetail_Success() throws Exception {
        mockMvc.perform(get("/fund-account/{fundAccountId}", testFundAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testFundAccount.getId()))
                .andExpect(jsonPath("$.data.accountName").value("测试资金账户"));
    }

    @Test
    void testUpdateFundAccountBalance_Success() throws Exception {
        FundAccountBalanceRequest request = new FundAccountBalanceRequest();
        request.setCurrentBalance(new BigDecimal("1500000.00"));

        mockMvc.perform(put("/fund-account/{fundAccountId}/balance", testFundAccount.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
