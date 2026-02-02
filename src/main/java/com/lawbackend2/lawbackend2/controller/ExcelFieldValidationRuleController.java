package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleResponse;
import com.lawbackend2.lawbackend2.dto.ExcelFieldValidationRuleUpdateRequest;
import com.lawbackend2.lawbackend2.service.ExcelFieldValidationRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Excel字段验证规则管理")
@RestController
@RequestMapping("/api/excel-field-validation-rules")
public class ExcelFieldValidationRuleController {
    
    @Autowired
    private ExcelFieldValidationRuleService ruleService;
    
    @Operation(summary = "创建字段验证规则")
    @PostMapping
    public ResponseEntity<ExcelFieldValidationRuleResponse> createRule(
            @Parameter(description = "验证规则创建请求") @RequestBody ExcelFieldValidationRuleCreateRequest request,
            @Parameter(description = "创建者用户ID") @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        log.info("创建字段验证规则: fieldName={}, ruleType={}, createdBy={}", 
                 request.getFieldName(), request.getRuleType(), userId);
        
        ExcelFieldValidationRuleResponse response = ruleService.createRule(request, userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "更新字段验证规则")
    @PutMapping("/{id}")
    public ResponseEntity<ExcelFieldValidationRuleResponse> updateRule(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @Parameter(description = "验证规则更新请求") @RequestBody ExcelFieldValidationRuleUpdateRequest request,
            @Parameter(description = "修改者用户ID") @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        log.info("更新字段验证规则: id={}, updatedBy={}", id, userId);
        
        ExcelFieldValidationRuleResponse response = ruleService.updateRule(id, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据ID查询字段验证规则")
    @GetMapping("/{id}")
    public ResponseEntity<ExcelFieldValidationRuleResponse> getRuleById(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        
        log.info("查询字段验证规则: id={}", id);
        
        ExcelFieldValidationRuleResponse response = ruleService.getRuleById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "查询所有字段验证规则")
    @GetMapping
    public ResponseEntity<List<ExcelFieldValidationRuleResponse>> getAllRules() {
        
        log.info("查询所有字段验证规则");
        
        List<ExcelFieldValidationRuleResponse> response = ruleService.getAllRules();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "查询所有启用的字段验证规则")
    @GetMapping("/active")
    public ResponseEntity<List<ExcelFieldValidationRuleResponse>> getAllActiveRules() {
        
        log.info("查询所有启用的字段验证规则");
        
        List<ExcelFieldValidationRuleResponse> response = ruleService.getAllActiveRules();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据字段名查询验证规则")
    @GetMapping("/field/{fieldName}")
    public ResponseEntity<List<ExcelFieldValidationRuleResponse>> getRulesByFieldName(
            @Parameter(description = "字段名") @PathVariable String fieldName) {
        
        log.info("查询字段的验证规则: fieldName={}", fieldName);
        
        List<ExcelFieldValidationRuleResponse> response = ruleService.getRulesByFieldName(fieldName);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据字段名查询启用的验证规则")
    @GetMapping("/field/{fieldName}/active")
    public ResponseEntity<List<ExcelFieldValidationRuleResponse>> getActiveRulesByFieldName(
            @Parameter(description = "字段名") @PathVariable String fieldName) {
        
        log.info("查询字段的启用验证规则: fieldName={}", fieldName);
        
        List<ExcelFieldValidationRuleResponse> response = ruleService.getActiveRulesByFieldName(fieldName);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "切换字段验证规则状态")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleRuleStatus(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean isActive) {
        
        log.info("切换字段验证规则状态: id={}, isActive={}", id, isActive);
        
        ruleService.toggleRuleStatus(id, isActive);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "删除字段验证规则")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        
        log.info("删除字段验证规则: id={}", id);
        
        ruleService.deleteRule(id);
        return ResponseEntity.ok().build();
    }
}
