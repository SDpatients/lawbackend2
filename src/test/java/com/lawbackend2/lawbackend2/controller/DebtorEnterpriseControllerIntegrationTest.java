package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.CaseReviewRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimReviewRequest;
import com.lawbackend2.lawbackend2.dto.DebtorCreateRequest;
import com.lawbackend2.lawbackend2.dto.DebtorUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DebtorEnterpriseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDebtor_Success() throws Exception {
        DebtorCreateRequest request = new DebtorCreateRequest();
        request.setCaseId(1L);
        request.setEnterpriseName("测试企业");
        request.setUnifiedSocialCreditCode("91110000MA01234567");
        request.setLegalRepresentative("张三");
        request.setContactPhone("13800138000");
        request.setContactPerson("李四");
        request.setEstablishmentDate(LocalDate.now());
        request.setRegisteredCapital(new BigDecimal("1000000.00"));
        request.setBusinessScope("技术开发");
        request.setEnterpriseType("有限责任公司");
        request.setIndustry("软件和信息技术服务业");
        request.setRegisteredAddress("北京市朝阳区");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/debtor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.debtorId").exists());
    }

    @Test
    void testGetDebtorById_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/debtor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testUpdateDebtor_Success() throws Exception {
        DebtorUpdateRequest request = new DebtorUpdateRequest();
        request.setEnterpriseName("更新后的企业名称");
        request.setContactPhone("13900139000");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/debtor/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
