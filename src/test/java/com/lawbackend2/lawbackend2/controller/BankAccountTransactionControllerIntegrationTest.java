package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountTransactionUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.entity.BankAccountTransaction;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
import com.lawbackend2.lawbackend2.repository.BankAccountTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BankAccountTransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankAccountTransactionRepository transactionRepository;

    private BankAccount testAccount;
    private BankAccountTransaction testTransaction;

    @BeforeEach
    void setUp() {
        testAccount = new BankAccount();
        testAccount.setAccountName("测试账户");
        testAccount.setBankName("中国银行");
        testAccount.setAccountNumber("1234567890123456");
        testAccount.setAccountType("基本户");
        testAccount.setCurrency("CNY");
        testAccount.setCurrentBalance(new BigDecimal("1000000.00"));
        testAccount.setOpeningDate(LocalDate.now());
        testAccount.setPassword("encryptedPassword");
        testAccount.setStatus("ACTIVE");
        testAccount.setCaseId(1L);
        testAccount = bankAccountRepository.save(testAccount);

        testTransaction = new BankAccountTransaction();
        testTransaction.setAccountId(testAccount.getId());
        testTransaction.setTransactionType("IN");
        testTransaction.setAmount(new BigDecimal("500000.00"));
        testTransaction.setTransactionDate(LocalDate.now());
        testTransaction.setSummary("测试交易");
        testTransaction.setBusinessType("收款");
        testTransaction.setCounterpartyAccount("6222021234567890");
        testTransaction.setCounterpartyName("张三");
        testTransaction.setBalanceAfter(new BigDecimal("1500000.00"));
        testTransaction.setRemark("测试备注");
        testTransaction.setCaseId(1L);
        testTransaction.setStatus("ACTIVE");
        testTransaction = transactionRepository.save(testTransaction);
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        bankAccountRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testCreateTransaction_Success() throws Exception {
        BankAccountTransactionCreateRequest request = new BankAccountTransactionCreateRequest();
        request.setAccountId(testAccount.getId());
        request.setTransactionType("IN");
        request.setAmount(new BigDecimal("100000.00"));
        request.setTransactionDate(LocalDate.now());
        request.setSummary("新测试交易");
        request.setBusinessType("收款");
        request.setCounterpartyAccount("6222029876543210");
        request.setCounterpartyName("李四");
        request.setBalanceAfter(new BigDecimal("1600000.00"));
        request.setRemark("新测试备注");
        request.setCaseId(1L);

        mockMvc.perform(post("/bank-account-transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.transactionId").isNumber());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetTransactionList_Success() throws Exception {
        mockMvc.perform(get("/bank-account-transaction/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetTransactionList_WithAccountId_Success() throws Exception {
        mockMvc.perform(get("/bank-account-transaction/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("accountId", testAccount.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetTransactionList_WithTransactionType_Success() throws Exception {
        mockMvc.perform(get("/bank-account-transaction/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("transactionType", "IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetTransactionList_WithDateRange_Success() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/bank-account-transaction/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetTransactionDetail_Success() throws Exception {
        mockMvc.perform(get("/bank-account-transaction/{transactionId}", testTransaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testTransaction.getId()))
                .andExpect(jsonPath("$.data.transactionType").value("IN"))
                .andExpect(jsonPath("$.data.amount").value(500000.00));
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testUpdateTransaction_Success() throws Exception {
        BankAccountTransactionUpdateRequest request = new BankAccountTransactionUpdateRequest();
        request.setAmount(new BigDecimal("600000.00"));
        request.setSummary("更新后的交易摘要");
        request.setRemark("更新后的备注");

        mockMvc.perform(put("/bank-account-transaction/{transactionId}", testTransaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testDeleteTransaction_Success() throws Exception {
        mockMvc.perform(delete("/bank-account-transaction/{transactionId}", testTransaction.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetAccountTransactions_Success() throws Exception {
        mockMvc.perform(get("/bank-account/{accountId}/transactions", testAccount.getId())
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetAccountTransactions_WithTransactionType_Success() throws Exception {
        mockMvc.perform(get("/bank-account/{accountId}/transactions", testAccount.getId())
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("transactionType", "IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ADMIN"})
    void testGetAccountTransactions_WithDateRange_Success() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/bank-account/{accountId}/transactions", testAccount.getId())
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }
}
