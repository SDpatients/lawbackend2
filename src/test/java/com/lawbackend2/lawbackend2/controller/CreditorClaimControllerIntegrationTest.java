package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.CreditorClaimCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CreditorClaim;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.CreditorClaimRepository;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CreditorClaimControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreditorClaimRepository creditorClaimRepository;

    @Autowired
    private BankruptCaseRepository bankruptCaseRepository;

    private BankruptCase testCase;
    private CreditorClaim testClaim;

    @BeforeEach
    void setUp() {
        testCase = new BankruptCase();
        testCase.setCaseNumber("TEST001");
        testCase.setCaseName("测试案件");
        testCase.setAcceptanceDate(LocalDate.now());
        testCase = bankruptCaseRepository.save(testCase);

        testClaim = new CreditorClaim();
        testClaim.setCaseId(testCase.getId());
        testClaim.setCaseName("测试案件");
        testClaim.setCreditorName("测试债权人");
        testClaim.setCreditorType("企业");
        testClaim.setClaimType("普通债权");
        testClaim.setTotalAmount(new BigDecimal("1000000.00"));
        testClaim.setRegistrationStatus("PENDING");
        testClaim = creditorClaimRepository.save(testClaim);
    }

    @AfterEach
    void tearDown() {
        creditorClaimRepository.deleteAll();
        bankruptCaseRepository.deleteAll();
    }

    @Test
    void testCreateClaim_Success() throws Exception {
        CreditorClaimCreateRequest request = new CreditorClaimCreateRequest();
        request.setCaseId(testCase.getId());
        request.setCaseName("测试案件");
        request.setCreditorName("新债权人");
        request.setCreditorType("企业");
        request.setClaimType("普通债权");
        request.setTotalAmount(new BigDecimal("2000000.00"));

        mockMvc.perform(post("/creditor-claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.claimId").exists());
    }

    @Test
    void testGetClaimById_Success() throws Exception {
        mockMvc.perform(get("/creditor-claim/{claimId}", testClaim.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testClaim.getId()))
                .andExpect(jsonPath("$.data.creditorName").value("测试债权人"));
    }

    @Test
    void testGetClaimList_Success() throws Exception {
        mockMvc.perform(get("/creditor-claim/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testUpdateClaim_Success() throws Exception {
        CreditorClaimUpdateRequest request = new CreditorClaimUpdateRequest();
        request.setCreditorName("更新后的债权人");
        request.setTotalAmount(new BigDecimal("3000000.00"));

        mockMvc.perform(put("/creditor-claim/{claimId}", testClaim.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
