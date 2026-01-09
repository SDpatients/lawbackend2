package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
class CreditorClaimReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testReviewClaim_Registered_Success() throws Exception {
        CreditorClaimReviewRequest request = new CreditorClaimReviewRequest();
        request.setRegistrationStatus("REGISTERED");
        request.setReviewOpinion("审核通过");
        request.setClaimNatureManager("有担保债权");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/creditor-claim/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testReviewClaim_Rejected_Success() throws Exception {
        CreditorClaimReviewRequest request = new CreditorClaimReviewRequest();
        request.setRegistrationStatus("REJECTED");
        request.setReviewOpinion("审核驳回");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/creditor-claim/1/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetClaimReviewStatus_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/creditor-claim/1/review-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.registrationStatus").exists());
    }
}
