package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.entity.FundOperationLog;
import com.lawbackend2.lawbackend2.repository.FundOperationLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FundOperationLogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FundOperationLogRepository fundOperationLogRepository;

    private FundOperationLog testLog;

    @BeforeEach
    void setUp() {
        testLog = new FundOperationLog();
        testLog.setCaseId(1L);
        testLog.setOperationType("CREATE");
        testLog.setOperationContent("测试操作");
        testLog.setOperatorId(1L);
        testLog.setOperationTime(LocalDateTime.now());
        testLog.setIpAddress("127.0.0.1");
        testLog.setBrowserInfo("Chrome");
        testLog.setStatus("ACTIVE");
        testLog = fundOperationLogRepository.save(testLog);
    }

    @AfterEach
    void tearDown() {
        fundOperationLogRepository.deleteAll();
    }

    @Test
    void testGetFundOperationLogList_Success() throws Exception {
        mockMvc.perform(get("/fund-operation-log/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }
}
