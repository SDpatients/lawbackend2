package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.CaseCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseStatusUpdateRequest;
import com.lawbackend2.lawbackend2.dto.CaseUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
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
class BankruptCaseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankruptCaseRepository bankruptCaseRepository;

    private BankruptCase testCase;

    @BeforeEach
    void setUp() {
        testCase = new BankruptCase();
        testCase.setCaseNumber("TEST001");
        testCase.setCaseName("测试案件");
        testCase.setAcceptanceDate(LocalDate.now());
        testCase.setCaseStatus("PENDING");
        testCase.setCaseProgress("FIRST");
        testCase = bankruptCaseRepository.save(testCase);
    }

    @AfterEach
    void tearDown() {
        bankruptCaseRepository.deleteAll();
    }

    @Test
    void testCreateCase_Success() throws Exception {
        CaseCreateRequest request = new CaseCreateRequest();
        request.setCaseNumber("TEST002");
        request.setCaseName("新测试案件");
        request.setAcceptanceDate(LocalDate.now());

        mockMvc.perform(post("/case")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.caseNumber").value("TEST002"));
    }

    @Test
    void testGetCaseById_Success() throws Exception {
        mockMvc.perform(get("/case/{caseId}", testCase.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testCase.getId()))
                .andExpect(jsonPath("$.data.caseNumber").value("TEST001"));
    }

    @Test
    void testGetCaseList_Success() throws Exception {
        mockMvc.perform(get("/case/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testUpdateCase_Success() throws Exception {
        CaseUpdateRequest request = new CaseUpdateRequest();
        request.setCaseName("更新后的案件名称");

        mockMvc.perform(put("/case/{caseId}", testCase.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdateCaseStatus_Success() throws Exception {
        CaseStatusUpdateRequest request = new CaseStatusUpdateRequest();
        request.setCaseStatus("IN_PROGRESS");

        mockMvc.perform(put("/case/{caseId}/status", testCase.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
