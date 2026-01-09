package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.CreditorCreateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CreditorInfoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CreditorInfoRepository creditorInfoRepository;

    private CreditorInfo testCreditor;

    @BeforeEach
    void setUp() {
        testCreditor = new CreditorInfo();
        testCreditor.setCaseId(1L);
        testCreditor.setCreditorName("测试债权人");
        testCreditor.setCreditorType("企业");
        testCreditor.setContactPhone("13800138000");
        testCreditor.setRegisteredCapital(new BigDecimal("1000000.00"));
        testCreditor = creditorInfoRepository.save(testCreditor);
    }

    @AfterEach
    void tearDown() {
        creditorInfoRepository.deleteAll();
    }

    @Test
    void testCreateCreditor_Success() throws Exception {
        CreditorCreateRequest request = new CreditorCreateRequest();
        request.setCaseId(1L);
        request.setCreditorName("新债权人");
        request.setCreditorType("企业");
        request.setContactPhone("13900139000");

        mockMvc.perform(post("/creditor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.creditorId").exists());
    }

    @Test
    void testGetCreditorById_Success() throws Exception {
        mockMvc.perform(get("/creditor/{creditorId}", testCreditor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testCreditor.getId()))
                .andExpect(jsonPath("$.data.creditorName").value("测试债权人"));
    }

    @Test
    void testGetCreditorList_Success() throws Exception {
        mockMvc.perform(get("/creditor/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }
}
