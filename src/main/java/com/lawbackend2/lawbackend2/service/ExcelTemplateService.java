package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ExcelTemplateCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelTemplateUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ExcelImportTemplate;
import java.util.List;
import java.util.Map;

public interface ExcelTemplateService {
    
    ExcelImportTemplate createTemplate(ExcelTemplateCreateRequest request, Long userId);
    
    ExcelImportTemplate updateTemplate(Long id, ExcelTemplateUpdateRequest request, Long userId);
    
    void deleteTemplate(Long id, Long userId);
    
    ExcelImportTemplate getTemplate(Long id);
    
    ExcelImportTemplate getDefaultTemplate();
    
    List<ExcelImportTemplate> getAllTemplates();
    
    void setDefaultTemplate(Long id, Long userId);
    
    Map<String, String> getFieldMappings(String templateCode);
}
