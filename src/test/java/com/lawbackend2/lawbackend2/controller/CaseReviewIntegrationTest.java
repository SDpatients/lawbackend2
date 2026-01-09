package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.CaseReviewRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimReviewRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CaseReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testReviewCase_Approved_Success() throws Exception {
        CaseReviewRequest request = new CaseReviewRequest();
        request.setReviewStatus("APPROVED");
        request.setReviewOpinion("审核通过");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/case/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testReviewCase_Rejected_Success() throws Exception {
        CaseReviewRequest request = new CaseReviewRequest();
        request.setReviewStatus("REJECTED");
        request.setReviewOpinion("审核驳回");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/case/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetReviewStatus_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/case/1/review-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reviewStatus").exists());
    }
}
