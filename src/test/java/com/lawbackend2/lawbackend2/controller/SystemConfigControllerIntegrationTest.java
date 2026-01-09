package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.response.ApiResponse;
import com.lawbackend2.lawbackend2.entity.SystemConfig;
import com.lawbackend2.lawbackend2.service.SystemConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SystemConfigControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SystemConfigService systemConfigService;

    private static final String TEST_CONFIG_KEY = "test.config.key";
    private static final String TEST_CONFIG_VALUE = "test.value";
    private static final Long TEST_CONFIG_ID = 1L;

    @BeforeEach
    void setUp() {
        SystemConfig mockConfig = SystemConfig.builder()
                .id(TEST_CONFIG_ID)
                .configKey(TEST_CONFIG_KEY)
                .configValue(TEST_CONFIG_VALUE)
                .configDesc("Test config")
                .configGroup("test")
                .status("ACTIVE")
                .sortOrder(0)
                .build();

        when(systemConfigService.getConfigByKey(TEST_CONFIG_KEY)).thenReturn(mockConfig);
        when(systemConfigService.getAllConfigs()).thenReturn(List.of(mockConfig));
        when(systemConfigService.updateConfig(anyString(), anyString())).thenReturn(mockConfig);
    }

    @Test
    void testGetAllConfigs_Success() throws Exception {
        mockMvc.perform(get("/config")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].configKey").value(TEST_CONFIG_KEY));
    }

    @Test
    void testGetConfigByKey_Success() throws Exception {
        mockMvc.perform(get("/config/" + TEST_CONFIG_KEY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.configKey").value(TEST_CONFIG_KEY))
                .andExpect(jsonPath("$.data.configValue").value(TEST_CONFIG_VALUE));
    }

    @Test
    void testGetConfigByKey_NotFound() throws Exception {
        when(systemConfigService.getConfigByKey("nonexistent.key")).thenReturn(null);

        mockMvc.perform(get("/config/nonexistent.key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("配置不存在"));
    }

    @Test
    void testCreateConfig_Success() throws Exception {
        SystemConfig newConfig = SystemConfig.builder()
                .configKey("new.config.key")
                .configValue("new.value")
                .configDesc("New config")
                .configGroup("test")
                .status("ACTIVE")
                .build();

        when(systemConfigService.createConfig(any(SystemConfig.class))).thenReturn(newConfig);

        mockMvc.perform(post("/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newConfig)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.configKey").value("new.config.key"));
    }

    @Test
    void testUpdateConfig_Success() throws Exception {
        mockMvc.perform(put("/config/" + TEST_CONFIG_KEY)
                        .param("configValue", "updated.value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void testUpdateConfigStatus_Success() throws Exception {
        mockMvc.perform(put("/config/" + TEST_CONFIG_ID + "/status")
                        .param("status", "INACTIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }

    @Test
    void testDeleteConfig_Success() throws Exception {
        mockMvc.perform(delete("/config/" + TEST_CONFIG_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
    }
}
