package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.BankAccountCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountPasswordRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.BankAccountUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankAccount;
import com.lawbackend2.lawbackend2.repository.BankAccountRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BankAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private BankAccount testAccount;

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
        testAccount = bankAccountRepository.save(testAccount);
    }

    @AfterEach
    void tearDown() {
        bankAccountRepository.deleteAll();
    }

    @Test
    void testCreateBankAccount_Success() throws Exception {
        BankAccountCreateRequest request = new BankAccountCreateRequest();
        request.setAccountName("新测试账户");
        request.setBankName("工商银行");
        request.setAccountNumber("9876543210987654");
        request.setAccountType("一般户");
        request.setCurrency("CNY");
        request.setCurrentBalance(new BigDecimal("2000000.00"));
        request.setOpeningDate(LocalDate.now());
        request.setPassword("testPassword123");

        mockMvc.perform(post("/bank-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accountId").isNumber());
    }

    @Test
    void testGetBankAccountList_Success() throws Exception {
        mockMvc.perform(get("/bank-account/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetBankAccountList_WithAccountName_Success() throws Exception {
        mockMvc.perform(get("/bank-account/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("accountName", "测试"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetBankAccountDetail_Success() throws Exception {
        mockMvc.perform(get("/bank-account/{accountId}", testAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testAccount.getId()))
                .andExpect(jsonPath("$.data.accountName").value("测试账户"));
    }

    @Test
    void testUpdateBankAccount_Success() throws Exception {
        BankAccountUpdateRequest request = new BankAccountUpdateRequest();
        request.setAccountName("更新后的账户名称");
        request.setCurrentBalance(new BigDecimal("1500000.00"));
        request.setAccountNumber("6632102522161219");
        request.setAccountType("基本户");
        request.setPassword("newPassword123");
        request.setCurrency("CNY");
        request.setOpeningDate(LocalDate.now());
        request.setClosingDate(null);
        request.setStatus("ACTIVE");
        request.setBankName("中国农业银行");
        request.setCaseId(18L);

        mockMvc.perform(put("/bank-account/{accountId}", testAccount.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateBankAccountStatus_Success() throws Exception {
        BankAccountStatusRequest request = new BankAccountStatusRequest();
        request.setStatus("INACTIVE");

        mockMvc.perform(put("/bank-account/{accountId}/status", testAccount.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
