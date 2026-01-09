package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.SystemConfig;

import java.util.List;

public interface SystemConfigService {

    List<SystemConfig> getAllConfigs();

    List<SystemConfig> getConfigsByGroup(String configGroup);

    SystemConfig getConfigByKey(String configKey);

    String getConfigValue(String configKey);

    SystemConfig createConfig(SystemConfig config);

    SystemConfig updateConfig(String configKey, String configValue);

    void deleteConfig(Long configId);

    void updateConfigStatus(Long configId, String status);
}
