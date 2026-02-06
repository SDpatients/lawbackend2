package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.ExcelTemplateCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelTemplateUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ExcelImportTemplate;
import com.lawbackend2.lawbackend2.repository.ExcelImportTemplateRepository;
import com.lawbackend2.lawbackend2.service.ExcelTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExcelTemplateServiceImpl implements ExcelTemplateService {
    
    private final ExcelImportTemplateRepository templateRepository;
    
    public ExcelTemplateServiceImpl(ExcelImportTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportTemplate createTemplate(ExcelTemplateCreateRequest request, Long userId) {
        log.info("创建Excel导入模板 - 模板名称: {}, 模板编码: {}, 用户ID: {}", 
                request.getTemplateName(), request.getTemplateCode(), userId);
        
        ExcelImportTemplate template = new ExcelImportTemplate();
        template.setTemplateName(request.getTemplateName());
        template.setTemplateCode(request.getTemplateCode());
        template.setDescription(request.getDescription());
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            String mappingsJson = mapper.writeValueAsString(request.getFieldMappings());
            template.setFieldMappings(mappingsJson);
        } catch (Exception e) {
            log.error("序列化字段映射失败", e);
            template.setFieldMappings("{}");
        }
        
        template.setIsDefault(false);
        template.setIsActive(true);
        template.setCreatedBy(userId);
        
        ExcelImportTemplate saved = templateRepository.save(template);
        log.info("Excel导入模板创建成功 - 模板ID: {}", saved.getId());
        
        return saved;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExcelImportTemplate updateTemplate(Long id, ExcelTemplateUpdateRequest request, Long userId) {
        log.info("更新Excel导入模板 - 模板ID: {}, 用户ID: {}", id, userId);
        
        ExcelImportTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("模板不存在"));
        
        if (request.getTemplateName() != null) {
            template.setTemplateName(request.getTemplateName());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getFieldMappings() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String mappingsJson = mapper.writeValueAsString(request.getFieldMappings());
                template.setFieldMappings(mappingsJson);
            } catch (Exception e) {
                log.error("序列化字段映射失败", e);
            }
        }
        
        if (request.getIsActive() != null) {
            template.setIsActive(request.getIsActive());
        }
        
        template.setUpdatedBy(userId);
        
        ExcelImportTemplate saved = templateRepository.save(template);
        log.info("Excel导入模板更新成功 - 模板ID: {}", saved.getId());
        
        return saved;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id, Long userId) {
        log.info("删除Excel导入模板 - 模板ID: {}, 用户ID: {}", id, userId);
        
        ExcelImportTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("模板不存在"));
        
        templateRepository.delete(template);
        log.info("Excel导入模板删除成功 - 模板ID: {}", id);
    }
    
    @Override
    public ExcelImportTemplate getTemplate(Long id) {
        return templateRepository.findById(id).orElse(null);
    }
    
    @Override
    public ExcelImportTemplate getDefaultTemplate() {
        return templateRepository.findByIsDefaultTrue().stream()
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<ExcelImportTemplate> getAllTemplates() {
        return templateRepository.findByIsActiveTrueOrderByIsDefaultDesc();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultTemplate(Long id, Long userId) {
        log.info("设置默认模板 - 模板ID: {}, 用户ID: {}", id, userId);
        
        List<ExcelImportTemplate> allTemplates = getAllTemplates();
        
        for (ExcelImportTemplate template : allTemplates) {
            template.setIsDefault(template.getId().equals(id));
        }
        
        templateRepository.saveAll(allTemplates);
        log.info("默认模板设置成功 - 模板ID: {}", id);
    }
    
    @Override
    public Map<String, String> getFieldMappings(String templateCode) {
        ExcelImportTemplate template = templateRepository.findByTemplateCode(templateCode);
        if (template == null || template.getFieldMappings() == null) {
            log.warn("未找到模板或模板字段映射为空 - 模板编码: {}", templateCode);
            return new HashMap<>();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(template.getFieldMappings(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.error("解析模板字段映射失败", e);
            return new HashMap<>();
        }
    }
}
