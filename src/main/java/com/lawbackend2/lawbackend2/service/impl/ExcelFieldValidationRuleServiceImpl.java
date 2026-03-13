package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleResponse;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ExcelFieldValidationRule;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ExcelFieldValidationRuleRepository;
import com.lawbackend2.lawbackend2.service.ExcelFieldValidationRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExcelFieldValidationRuleServiceImpl implements ExcelFieldValidationRuleService {
    
    @Autowired
    private ExcelFieldValidationRuleRepository ruleRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public ExcelFieldValidationRuleResponse createRule(ExcelFieldValidationRuleCreateRequest request, Long createdBy) {
        log.info("创建Excel字段验证规则: fieldName={}, ruleType={}, createdBy={}", 
                 request.getFieldName(), request.getRuleType(), createdBy);
        
        ExcelFieldValidationRule rule = new ExcelFieldValidationRule();
        rule.setFieldName(request.getFieldName());
        rule.setRuleType(request.getRuleType());
        rule.setRuleValue(request.getRuleValue());
        rule.setErrorMessage(request.getErrorMessage());
        rule.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        rule.setPriority(request.getPriority() != null ? request.getPriority() : 100);
        rule.setCreatedBy(createdBy);
        rule.setCreatedTime(LocalDateTime.now());
        
        ExcelFieldValidationRule savedRule = ruleRepository.save(rule);
        log.info("Excel字段验证规则创建成功: id={}", savedRule.getId());
        
        return convertToResponse(savedRule);
    }
    
    @Override
    @Transactional
    public ExcelFieldValidationRuleResponse updateRule(Long id, ExcelFieldValidationRuleUpdateRequest request, Long updatedBy) {
        log.info("更新Excel字段验证规则: id={}, updatedBy={}", id, updatedBy);
        
        ExcelFieldValidationRule rule = ruleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("验证规则不存在: " + id));
        
        if (request.getFieldName() != null) {
            rule.setFieldName(request.getFieldName());
        }
        if (request.getRuleType() != null) {
            rule.setRuleType(request.getRuleType());
        }
        if (request.getRuleValue() != null) {
            rule.setRuleValue(request.getRuleValue());
        }
        if (request.getErrorMessage() != null) {
            rule.setErrorMessage(request.getErrorMessage());
        }
        if (request.getIsActive() != null) {
            rule.setIsActive(request.getIsActive());
        }
        if (request.getPriority() != null) {
            rule.setPriority(request.getPriority());
        }
        
        ExcelFieldValidationRule savedRule = ruleRepository.save(rule);
        log.info("Excel字段验证规则更新成功: id={}", savedRule.getId());
        
        return convertToResponse(savedRule);
    }
    
    @Override
    public ExcelFieldValidationRuleResponse getRuleById(Long id) {
        log.info("查询Excel字段验证规则: id={}", id);
        
        ExcelFieldValidationRule rule = ruleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("验证规则不存在: " + id));
        
        return convertToResponse(rule);
    }
    
    @Override
    public List<ExcelFieldValidationRuleResponse> getRulesByFieldName(String fieldName) {
        log.info("查询字段的验证规则: fieldName={}", fieldName);
        
        List<ExcelFieldValidationRule> rules = ruleRepository.findByFieldName(fieldName);
        return rules.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ExcelFieldValidationRuleResponse> getActiveRulesByFieldName(String fieldName) {
        log.info("查询字段的启用验证规则: fieldName={}", fieldName);
        
        List<ExcelFieldValidationRule> rules = ruleRepository.findByFieldNameAndIsActiveOrderByPriority(fieldName);
        return rules.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ExcelFieldValidationRuleResponse> getAllActiveRules() {
        log.info("查询所有启用的验证规则");
        
        List<ExcelFieldValidationRule> rules = ruleRepository.findAllActiveRulesOrderByPriority();
        return rules.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ExcelFieldValidationRuleResponse> getAllRules() {
        log.info("查询所有验证规则");
        
        List<ExcelFieldValidationRule> rules = ruleRepository.findAll();
        return rules.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteRule(Long id) {
        log.info("删除Excel字段验证规则: id={}", id);
        
        if (!ruleRepository.existsById(id)) {
            throw new BusinessException(404, "验证规则不存在: " + id);
        }
        
        ruleRepository.deleteById(id);
        log.info("Excel字段验证规则删除成功: id={}", id);
    }
    
    @Override
    @Transactional
    public void toggleRuleStatus(Long id, Boolean isActive) {
        log.info("切换Excel字段验证规则状态: id={}, isActive={}", id, isActive);
        
        ExcelFieldValidationRule rule = ruleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("验证规则不存在: " + id));
        
        rule.setIsActive(isActive);
        ruleRepository.save(rule);
        log.info("Excel字段验证规则状态切换成功: id={}, isActive={}", id, isActive);
    }
    
    private ExcelFieldValidationRuleResponse convertToResponse(ExcelFieldValidationRule rule) {
        ExcelFieldValidationRuleResponse response = new ExcelFieldValidationRuleResponse();
        response.setId(rule.getId());
        response.setFieldName(rule.getFieldName());
        response.setRuleType(rule.getRuleType());
        response.setRuleValue(rule.getRuleValue());
        response.setErrorMessage(rule.getErrorMessage());
        response.setIsActive(rule.getIsActive());
        response.setPriority(rule.getPriority());
        response.setCreatedBy(rule.getCreatedBy());
        
        if (rule.getCreatedTime() != null) {
            response.setCreatedTime(rule.getCreatedTime().format(DATE_FORMATTER));
        }
        
        return response;
    }
}
