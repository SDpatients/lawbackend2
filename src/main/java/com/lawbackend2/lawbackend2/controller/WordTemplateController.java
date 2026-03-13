package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.service.impl.WordTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "Word 模板生成接口", description = "Word 模板生成和管理相关接口")
@RestController
@RequestMapping("/api/template")
public class WordTemplateController {

    @Autowired
    private WordTemplateService templateService;

    @PostMapping("/generate/data-report")
    @Operation(summary = "生成数据报告模板")
    public Result<Map<String, String>> generateDataReportTemplate(@RequestParam String templateName) {
        try {
            String templatePath = templateService.createDataReportTemplate(templateName);
            Map<String, String> result = new HashMap<>();
            result.put("templatePath", templatePath);
            result.put("templateName", templateName);
            result.put("message", "数据报告模板生成成功");
            return Result.success(result);
        } catch (IOException e) {
            log.error("生成数据报告模板失败", e);
            return Result.error("生成模板失败：" + e.getMessage());
        }
    }

    @PostMapping("/generate/custom")
    @Operation(summary = "生成自定义模板")
    public Result<Map<String, String>> generateCustomTemplate(
            @RequestParam String templateName,
            @RequestParam String title,
            @RequestParam String[] sections) {
        try {
            String templatePath = templateService.createCustomTemplate(templateName, title, sections);
            Map<String, String> result = new HashMap<>();
            result.put("templatePath", templatePath);
            result.put("templateName", templateName);
            result.put("message", "自定义模板生成成功");
            return Result.success(result);
        } catch (IOException e) {
            log.error("生成自定义模板失败", e);
            return Result.error("生成模板失败：" + e.getMessage());
        }
    }

    @PostMapping("/fill")
    @Operation(summary = "填充模板数据")
    public ResponseEntity<byte[]> fillTemplate(
            @RequestParam String templatePath,
            @RequestBody Map<String, Object> data) {
        try {
            byte[] filledContent = templateService.fillTemplateWithData(templatePath, data);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", "filled_template.docx");
            
            return new ResponseEntity<>(filledContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("填充模板失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/export/sample")
    @Operation(summary = "导出示例报告")
    public ResponseEntity<byte[]> exportSampleReport() {
        try {
            byte[] reportBytes = templateService.generateReportBytes("sample", templateService.createSampleData());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", "sample_report.docx");
            
            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("导出示例报告失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/generate-and-fill")
    @Operation(summary = "生成模板并填充数据")
    public ResponseEntity<byte[]> generateAndFillTemplate(
            @RequestParam String templateType,
            @RequestBody Map<String, Object> data) {
        try {
            String templatePath;
            switch (templateType.toLowerCase()) {
                case "legal":
                    templatePath = "templates/legal_template.docx";
                    break;
                case "project":
                    templatePath = "templates/project_template.docx";
                    break;
                case "meeting":
                    templatePath = "templates/meeting_template.docx";
                    break;
                default:
                    templatePath = "templates/data_report_template.docx";
            }
            
            byte[] filledContent = templateService.fillTemplateWithData(templatePath, data);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            String fileName = new String((templateType + "_report.docx").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            headers.setContentDispositionFormData("attachment", fileName);
            
            return new ResponseEntity<>(filledContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("生成并填充模板失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sample-data")
    @Operation(summary = "获取示例数据")
    public Result<Map<String, Object>> getSampleData() {
        Map<String, Object> data = templateService.createSampleData();
        return Result.success(data);
    }

    @DeleteMapping("/cleanup")
    @Operation(summary = "清理临时模板")
    public Result<String> cleanupTempTemplates() {
        templateService.cleanupTempTemplates();
        return Result.success("临时模板清理完成");
    }
}
