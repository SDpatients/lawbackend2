package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.UserRegisterRequest;
import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.dto.response.UserLoginResponse;
import com.lawbackend2.lawbackend2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "TestPassword123";
    private static final String TEST_MOBILE = "13800138000";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_REAL_NAME = "测试用户";
    private static final String TEST_SMS_CODE = "123456";

    @BeforeEach
    void setUp() {
        UserLoginResponse mockResponse = UserLoginResponse.builder()
                .userId(1L)
                .username(TEST_USERNAME)
                .realName(TEST_REAL_NAME)
                .accessToken("test.jwt.token")
                .build();

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(mockResponse);
    }

    @Test
    void testRegister_Success() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .realName(TEST_REAL_NAME)
                .mobile(TEST_MOBILE)
                .email(TEST_EMAIL)
                .smsCode(TEST_SMS_CODE)
                .build();

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testRegister_InvalidRequest() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("")
                .password("123")
                .realName(TEST_REAL_NAME)
                .mobile(TEST_MOBILE)
                .email(TEST_EMAIL)
                .smsCode(TEST_SMS_CODE)
                .build();

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_MissingRequiredFields() throws Exception {
        String invalidJson = "{\"username\":\"test\"}";

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
