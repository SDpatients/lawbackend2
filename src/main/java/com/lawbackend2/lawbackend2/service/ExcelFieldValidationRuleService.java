package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleResponse;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleUpdateRequest;

import java.util.List;

public interface ExcelFieldValidationRuleService {
    
    ExcelFieldValidationRuleResponse createRule(ExcelFieldValidationRuleCreateRequest request, Long createdBy);
    
    ExcelFieldValidationRuleResponse updateRule(Long id, ExcelFieldValidationRuleUpdateRequest request, Long updatedBy);
    
    ExcelFieldValidationRuleResponse getRuleById(Long id);
    
    List<ExcelFieldValidationRuleResponse> getRulesByFieldName(String fieldName);
    
    List<ExcelFieldValidationRuleResponse> getActiveRulesByFieldName(String fieldName);
    
    List<ExcelFieldValidationRuleResponse> getAllActiveRules();
    
    List<ExcelFieldValidationRuleResponse> getAllRules();
    
    void deleteRule(Long id);
    
    void toggleRuleStatus(Long id, Boolean isActive);
}
