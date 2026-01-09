package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.SystemConfig;
import com.lawbackend2.lawbackend2.repository.SystemConfigRepository;
import com.lawbackend2.lawbackend2.service.SystemConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SystemConfigServiceImplTest {

    @Mock
    private SystemConfigRepository systemConfigRepository;

    @InjectMocks
    private SystemConfigServiceImpl systemConfigService;

    private static final String TEST_CONFIG_KEY = "test.config.key";
    private static final String TEST_CONFIG_VALUE = "test.value";
    private static final String TEST_CONFIG_GROUP = "test.group";
    private static final Long TEST_CONFIG_ID = 1L;

    @BeforeEach
    void setUp() {
        SystemConfig mockConfig = SystemConfig.builder()
                .id(TEST_CONFIG_ID)
                .configKey(TEST_CONFIG_KEY)
                .configValue(TEST_CONFIG_VALUE)
                .configDesc("Test config description")
                .configGroup(TEST_CONFIG_GROUP)
                .status("ACTIVE")
                .sortOrder(0)
                .build();

        when(systemConfigRepository.findByConfigKey(TEST_CONFIG_KEY))
                .thenReturn(Optional.of(mockConfig));
        when(systemConfigRepository.findById(TEST_CONFIG_ID))
                .thenReturn(Optional.of(mockConfig));
        when(systemConfigRepository.findAllActive(anyString()))
                .thenReturn(List.of(mockConfig));
        when(systemConfigRepository.findByConfigGroupAndStatus(anyString(), anyString()))
                .thenReturn(List.of(mockConfig));
        when(systemConfigRepository.existsByConfigKey(anyString()))
                .thenReturn(false);
        when(systemConfigRepository.save(any(SystemConfig.class)))
                .thenReturn(mockConfig);
    }

    @Test
    void testGetAllConfigs_Success() {
        List<SystemConfig> configs = systemConfigService.getAllConfigs();

        assertNotNull(configs);
        assertEquals(1, configs.size());
        assertEquals(TEST_CONFIG_KEY, configs.get(0).getConfigKey());
        verify(systemConfigRepository, times(1)).findAllActive("ACTIVE");
    }

    @Test
    void testGetConfigsByGroup_Success() {
        List<SystemConfig> configs = systemConfigService.getConfigsByGroup(TEST_CONFIG_GROUP);

        assertNotNull(configs);
        assertEquals(1, configs.size());
        assertEquals(TEST_CONFIG_GROUP, configs.get(0).getConfigGroup());
        verify(systemConfigRepository, times(1)).findByConfigGroupAndStatus(TEST_CONFIG_GROUP, "ACTIVE");
    }

    @Test
    void testGetConfigByKey_Success() {
        SystemConfig config = systemConfigService.getConfigByKey(TEST_CONFIG_KEY);

        assertNotNull(config);
        assertEquals(TEST_CONFIG_KEY, config.getConfigKey());
        assertEquals(TEST_CONFIG_VALUE, config.getConfigValue());
        verify(systemConfigRepository, times(1)).findByConfigKey(TEST_CONFIG_KEY);
    }

    @Test
    void testGetConfigByKey_NotFound() {
        when(systemConfigRepository.findByConfigKey("nonexistent.key"))
                .thenReturn(Optional.empty());

        SystemConfig config = systemConfigService.getConfigByKey("nonexistent.key");

        assertNull(config);
        verify(systemConfigRepository, times(1)).findByConfigKey("nonexistent.key");
    }

    @Test
    void testGetConfigValue_Success() {
        String configValue = systemConfigService.getConfigValue(TEST_CONFIG_KEY);

        assertNotNull(configValue);
        assertEquals(TEST_CONFIG_VALUE, configValue);
    }

    @Test
    void testGetConfigValue_NotFound() {
        when(systemConfigRepository.findByConfigKey("nonexistent.key"))
                .thenReturn(Optional.empty());

        String configValue = systemConfigService.getConfigValue("nonexistent.key");

        assertNull(configValue);
    }

    @Test
    void testCreateConfig_Success() {
        SystemConfig newConfig = SystemConfig.builder()
                .configKey("new.config.key")
                .configValue("new.value")
                .configDesc("New config description")
                .configGroup(TEST_CONFIG_GROUP)
                .status("ACTIVE")
                .build();

        when(systemConfigRepository.existsByConfigKey("new.config.key")).thenReturn(false);
        when(systemConfigRepository.save(newConfig)).thenReturn(newConfig);

        SystemConfig createdConfig = systemConfigService.createConfig(newConfig);

        assertNotNull(createdConfig);
        assertEquals("new.config.key", createdConfig.getConfigKey());
        verify(systemConfigRepository, times(1)).existsByConfigKey("new.config.key");
        verify(systemConfigRepository, times(1)).save(newConfig);
    }

    @Test
    void testCreateConfig_DuplicateKey() {
        when(systemConfigRepository.existsByConfigKey(TEST_CONFIG_KEY))
                .thenReturn(true);

        SystemConfig newConfig = SystemConfig.builder()
                .configKey(TEST_CONFIG_KEY)
                .configValue("new.value")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            systemConfigService.createConfig(newConfig);
        });

        assertTrue(exception.getMessage().contains("配置键已存在"));
        verify(systemConfigRepository, never()).save(any(SystemConfig.class));
    }

    @Test
    void testUpdateConfig_Success() {
        String newValue = "updated.value";

        SystemConfig updatedConfig = systemConfigService.updateConfig(TEST_CONFIG_KEY, newValue);

        assertNotNull(updatedConfig);
        assertEquals(newValue, updatedConfig.getConfigValue());
        verify(systemConfigRepository, times(1)).findByConfigKey(TEST_CONFIG_KEY);
        verify(systemConfigRepository, times(1)).save(any(SystemConfig.class));
    }

    @Test
    void testUpdateConfig_NotFound() {
        when(systemConfigRepository.findByConfigKey("nonexistent.key"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            systemConfigService.updateConfig("nonexistent.key", "new.value");
        });

        assertTrue(exception.getMessage().contains("配置不存在"));
    }

    @Test
    void testDeleteConfig_Success() {
        when(systemConfigRepository.findById(TEST_CONFIG_ID))
                .thenReturn(Optional.of(SystemConfig.builder()
                        .id(TEST_CONFIG_ID)
                        .configKey(TEST_CONFIG_KEY)
                        .build()));

        assertDoesNotThrow(() -> {
            systemConfigService.deleteConfig(TEST_CONFIG_ID);
        });

        verify(systemConfigRepository, times(1)).findById(TEST_CONFIG_ID);
        verify(systemConfigRepository, times(1)).save(any(SystemConfig.class));
    }

    @Test
    void testDeleteConfig_NotFound() {
        when(systemConfigRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            systemConfigService.deleteConfig(999L);
        });

        assertTrue(exception.getMessage().contains("配置不存在"));
    }

    @Test
    void testUpdateConfigStatus_Success() {
        assertDoesNotThrow(() -> {
            systemConfigService.updateConfigStatus(TEST_CONFIG_ID, "INACTIVE");
        });

        verify(systemConfigRepository, times(1)).findById(TEST_CONFIG_ID);
        verify(systemConfigRepository, times(1)).save(any(SystemConfig.class));
    }

    @Test
    void testUpdateConfigStatus_NotFound() {
        when(systemConfigRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            systemConfigService.updateConfigStatus(999L, "INACTIVE");
        });

        assertTrue(exception.getMessage().contains("配置不存在"));
    }
}
