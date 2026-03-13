package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.SystemConfig;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.SystemConfigRepository;
import com.lawbackend2.lawbackend2.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Override
    public List<SystemConfig> getAllConfigs() {
        List<SystemConfig> configs = systemConfigRepository.findAllActive("ACTIVE");
        log.info("查询所有系统配置，共{}条记录", configs.size());
        return configs;
    }

    @Override
    public List<SystemConfig> getConfigsByGroup(String configGroup) {
        List<SystemConfig> configs = systemConfigRepository.findByConfigGroupAndStatus(configGroup, "ACTIVE");
        log.info("查询系统配置组: {}, 共{}条记录", configGroup, configs.size());
        return configs;
    }

    @Override
    public SystemConfig getConfigByKey(String configKey) {
        Optional<SystemConfig> configOpt = systemConfigRepository.findByConfigKey(configKey);
        if (configOpt.isPresent()) {
            log.debug("获取系统配置: {} = {}", configKey, configOpt.get().getConfigValue());
            return configOpt.get();
        }
        log.warn("系统配置不存在: {}", configKey);
        return null;
    }

    @Override
    public String getConfigValue(String configKey) {
        SystemConfig config = getConfigByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional
    public SystemConfig createConfig(SystemConfig config) {
        if (systemConfigRepository.existsByConfigKey(config.getConfigKey())) {
            throw new BusinessException(400, "配置键已存在: " + config.getConfigKey());
        }

        SystemConfig savedConfig = systemConfigRepository.save(config);
        log.info("创建系统配置成功 - 配置键: {}, 配置值: {}", 
                savedConfig.getConfigKey(), savedConfig.getConfigValue());
        return savedConfig;
    }

    @Override
    @Transactional
    public SystemConfig updateConfig(String configKey, String configValue) {
        Optional<SystemConfig> configOpt = systemConfigRepository.findByConfigKey(configKey);
        if (!configOpt.isPresent()) {
            throw new BusinessException(404, "配置不存在: " + configKey);
        }

        SystemConfig config = configOpt.get();
        config.setConfigValue(configValue);
        
        SystemConfig updatedConfig = systemConfigRepository.save(config);
        log.info("更新系统配置成功 - 配置键: {}, 新值: {}", 
                configKey, configValue);
        return updatedConfig;
    }

    @Override
    @Transactional
    public void deleteConfig(Long configId) {
        Optional<SystemConfig> configOpt = systemConfigRepository.findById(configId);
        if (!configOpt.isPresent()) {
            throw new BusinessException(404, "配置不存在");
        }

        SystemConfig config = configOpt.get();
        config.setIsDeleted(true);
        config.setStatus("DELETED");
        systemConfigRepository.save(config);

        log.info("删除系统配置成功 - 配置键: {}", config.getConfigKey());
    }

    @Override
    @Transactional
    public void updateConfigStatus(Long configId, String status) {
        Optional<SystemConfig> configOpt = systemConfigRepository.findById(configId);
        if (!configOpt.isPresent()) {
            throw new BusinessException(404, "配置不存在");
        }

        SystemConfig config = configOpt.get();
        config.setStatus(status);
        systemConfigRepository.save(config);

        log.info("更新系统配置状态成功 - 配置键: {}, 新状态: {}", 
                config.getConfigKey(), status);
    }
}
