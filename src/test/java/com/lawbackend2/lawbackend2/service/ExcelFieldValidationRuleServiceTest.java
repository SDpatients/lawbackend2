package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleResponse;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ExcelFieldValidationRule;
import com.lawbackend2.lawbackend2.repository.ExcelFieldValidationRuleRepository;
import com.lawbackend2.lawbackend2.service.impl.ExcelFieldValidationRuleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcelFieldValidationRuleServiceTest {
    
    @Mock
    private ExcelFieldValidationRuleRepository ruleRepository;
    
    @InjectMocks
    private ExcelFieldValidationRuleServiceImpl ruleService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testCreateRule() {
        // 准备测试数据
        ExcelFieldValidationRuleCreateRequest request = new ExcelFieldValidationRuleCreateRequest();
        request.setFieldName("creditorName");
        request.setRuleType("REQUIRED");
        request.setErrorMessage("债权人名称不能为空");
        request.setIsActive(true);
        request.setPriority(100);
        
        ExcelFieldValidationRule rule = new ExcelFieldValidationRule();
        rule.setId(1L);
        rule.setFieldName("creditorName");
        rule.setRuleType("REQUIRED");
        rule.setErrorMessage("债权人名称不能为空");
        rule.setIsActive(true);
        rule.setPriority(100);
        rule.setCreatedBy(1L);
        rule.setCreatedTime(LocalDateTime.now());
        
        // 模拟方法调用
        when(ruleRepository.save(any(ExcelFieldValidationRule.class))).thenReturn(rule);
        
        // 执行测试
        ExcelFieldValidationRuleResponse response = ruleService.createRule(request, 1L);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("creditorName", response.getFieldName());
        assertEquals("REQUIRED", response.getRuleType());
        assertEquals("债权人名称不能为空", response.getErrorMessage());
        assertTrue(response.getIsActive());
        assertEquals(100, response.getPriority());
        
        verify(ruleRepository, times(1)).save(any(ExcelFieldValidationRule.class));
    }
    
    @Test
    void testUpdateRule() {
        // 准备测试数据
        Long ruleId = 1L;
        ExcelFieldValidationRuleUpdateRequest request = new ExcelFieldValidationRuleUpdateRequest();
        request.setErrorMessage("债权人名称不能为空（更新）");
        request.setIsActive(false);
        
        ExcelFieldValidationRule existingRule = new ExcelFieldValidationRule();
        existingRule.setId(ruleId);
        existingRule.setFieldName("creditorName");
        existingRule.setRuleType("REQUIRED");
        existingRule.setErrorMessage("债权人名称不能为空");
        existingRule.setIsActive(true);
        existingRule.setPriority(100);
        existingRule.setCreatedBy(1L);
        existingRule.setCreatedTime(LocalDateTime.now());
        
        ExcelFieldValidationRule updatedRule = new ExcelFieldValidationRule();
        updatedRule.setId(ruleId);
        updatedRule.setFieldName("creditorName");
        updatedRule.setRuleType("REQUIRED");
        updatedRule.setErrorMessage("债权人名称不能为空（更新）");
        updatedRule.setIsActive(false);
        updatedRule.setPriority(100);
        updatedRule.setCreatedBy(1L);
        updatedRule.setCreatedTime(LocalDateTime.now());
        
        // 模拟方法调用
        when(ruleRepository.findById(ruleId)).thenReturn(Optional.of(existingRule));
        when(ruleRepository.save(any(ExcelFieldValidationRule.class))).thenReturn(updatedRule);
        
        // 执行测试
        ExcelFieldValidationRuleResponse response = ruleService.updateRule(ruleId, request, 1L);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(ruleId, response.getId());
        assertEquals("creditorName", response.getFieldName());
        assertEquals("REQUIRED", response.getRuleType());
        assertEquals("债权人名称不能为空（更新）", response.getErrorMessage());
        assertFalse(response.getIsActive());
        assertEquals(100, response.getPriority());
        
        verify(ruleRepository, times(1)).findById(ruleId);
        verify(ruleRepository, times(1)).save(any(ExcelFieldValidationRule.class));
    }
    
    @Test
    void testUpdateRule_NotFound() {
        // 准备测试数据
        Long ruleId = 999L;
        ExcelFieldValidationRuleUpdateRequest request = new ExcelFieldValidationRuleUpdateRequest();
        
        // 模拟方法调用
        when(ruleRepository.findById(ruleId)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ruleService.updateRule(ruleId, request, 1L);
        });
        
        assertEquals("验证规则不存在: " + ruleId, exception.getMessage());
        verify(ruleRepository, times(1)).findById(ruleId);
        verify(ruleRepository, never()).save(any(ExcelFieldValidationRule.class));
    }
    
    @Test
    void testToggleRuleStatus() {
        // 准备测试数据
        Long ruleId = 1L;
        boolean newStatus = false;
        
        ExcelFieldValidationRule existingRule = new ExcelFieldValidationRule();
        existingRule.setId(ruleId);
        existingRule.setFieldName("creditorName");
        existingRule.setRuleType("REQUIRED");
        existingRule.setErrorMessage("债权人名称不能为空");
        existingRule.setIsActive(true);
        existingRule.setPriority(100);
        existingRule.setCreatedBy(1L);
        existingRule.setCreatedTime(LocalDateTime.now());
        
        ExcelFieldValidationRule updatedRule = new ExcelFieldValidationRule();
        updatedRule.setId(ruleId);
        updatedRule.setFieldName("creditorName");
        updatedRule.setRuleType("REQUIRED");
        updatedRule.setErrorMessage("债权人名称不能为空");
        updatedRule.setIsActive(newStatus);
        updatedRule.setPriority(100);
        updatedRule.setCreatedBy(1L);
        updatedRule.setCreatedTime(LocalDateTime.now());
        
        // 模拟方法调用
        when(ruleRepository.findById(ruleId)).thenReturn(Optional.of(existingRule));
        when(ruleRepository.save(any(ExcelFieldValidationRule.class))).thenReturn(updatedRule);
        
        // 执行测试
        ruleService.toggleRuleStatus(ruleId, newStatus);
        
        // 验证结果
        verify(ruleRepository, times(1)).findById(ruleId);
        verify(ruleRepository, times(1)).save(any(ExcelFieldValidationRule.class));
    }
    
    @Test
    void testDeleteRule() {
        // 准备测试数据
        Long ruleId = 1L;
        
        // 模拟方法调用
        when(ruleRepository.existsById(ruleId)).thenReturn(true);
        doNothing().when(ruleRepository).deleteById(ruleId);
        
        // 执行测试
        ruleService.deleteRule(ruleId);
        
        // 验证结果
        verify(ruleRepository, times(1)).existsById(ruleId);
        verify(ruleRepository, times(1)).deleteById(ruleId);
    }
    
    @Test
    void testDeleteRule_NotFound() {
        // 准备测试数据
        Long ruleId = 999L;
        
        // 模拟方法调用
        when(ruleRepository.existsById(ruleId)).thenReturn(false);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ruleService.deleteRule(ruleId);
        });
        
        assertEquals("验证规则不存在: " + ruleId, exception.getMessage());
        verify(ruleRepository, times(1)).existsById(ruleId);
        verify(ruleRepository, never()).deleteById(ruleId);
    }
}
