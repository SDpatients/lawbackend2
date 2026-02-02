package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.ExcelTemplateCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelTemplateUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ExcelImportTemplate;
import com.lawbackend2.lawbackend2.service.ExcelTemplateService;
import com.lawbackend2.lawbackend2.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Tag(name = "Excel导入模板管理")
@RestController
@RequestMapping("/excel-templates")
@Validated
public class ExcelTemplateController {
    
    private final ExcelTemplateService excelTemplateService;
    
    public ExcelTemplateController(ExcelTemplateService excelTemplateService) {
        this.excelTemplateService = excelTemplateService;
    }
    
    @Operation(summary = "创建Excel导入模板")
    @PostMapping
    public Result<ExcelImportTemplate> createTemplate(@Valid @RequestBody ExcelTemplateCreateRequest request) {
        Long userId = getCurrentUserId();
        ExcelImportTemplate template = excelTemplateService.createTemplate(request, userId);
        return Result.success(template);
    }
    
    @Operation(summary = "更新Excel导入模板")
    @PutMapping("/{id}")
    public Result<ExcelImportTemplate> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody ExcelTemplateUpdateRequest request) {
        Long userId = getCurrentUserId();
        ExcelImportTemplate template = excelTemplateService.updateTemplate(id, request, userId);
        return Result.success(template);
    }
    
    @Operation(summary = "删除Excel导入模板")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        excelTemplateService.deleteTemplate(id, userId);
        return Result.success();
    }
    
    @Operation(summary = "获取所有Excel导入模板")
    @GetMapping
    public Result<List<ExcelImportTemplate>> getAllTemplates() {
        List<ExcelImportTemplate> templates = excelTemplateService.getAllTemplates();
        return Result.success(templates);
    }
    
    @Operation(summary = "获取默认Excel导入模板")
    @GetMapping("/default")
    public Result<ExcelImportTemplate> getDefaultTemplate() {
        ExcelImportTemplate template = excelTemplateService.getDefaultTemplate();
        return Result.success(template);
    }
    
    @Operation(summary = "设置默认Excel导入模板")
    @PostMapping("/{id}/set-default")
    public Result<Void> setDefaultTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        excelTemplateService.setDefaultTemplate(id, userId);
        return Result.success();
    }
    
    @Operation(summary = "获取模板字段映射")
    @GetMapping("/{code}/mappings")
    public Result<java.util.Map<String, String>> getTemplateMappings(
            @Parameter(description = "模板编码") @PathVariable String code) {
        java.util.Map<String, String> mappings = excelTemplateService.getFieldMappings(code);
        return Result.success(mappings);
    }
    
    private Long getCurrentUserId() {
        return 1L;
    }
}
