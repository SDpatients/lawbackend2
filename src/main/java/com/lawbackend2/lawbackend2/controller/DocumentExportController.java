package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.DocumentExportHistory;
import com.lawbackend2.lawbackend2.entity.DocumentExportTemplate;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.service.DocumentExportService;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Tag(name = "文档导出模板管理")
@RestController
@RequestMapping("/document-templates")
@Validated
public class DocumentExportController {

    private final DocumentExportService documentExportService;

    @Autowired
    public DocumentExportController(DocumentExportService documentExportService) {
        this.documentExportService = documentExportService;
    }

    @Operation(summary = "创建文档导出模板")
    @PostMapping
    public Result<DocumentExportTemplate> createTemplate(@Valid @RequestBody DocumentTemplateCreateRequest request) {
        Long userId = getCurrentUserId();
        DocumentExportTemplate template = documentExportService.createTemplate(request, userId);
        return Result.success(template);
    }

    @Operation(summary = "更新文档导出模板")
    @PutMapping("/{id}")
    public Result<DocumentExportTemplate> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Valid @RequestBody DocumentTemplateUpdateRequest request) {
        Long userId = getCurrentUserId();
        DocumentExportTemplate template = documentExportService.updateTemplate(id, request, userId);
        return Result.success(template);
    }

    @Operation(summary = "删除文档导出模板")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        documentExportService.deleteTemplate(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public Result<DocumentExportTemplate> getTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
        DocumentExportTemplate template = documentExportService.getTemplate(id);
        return Result.success(template);
    }

    @Operation(summary = "获取模板详情(包含字段映射)")
    @GetMapping("/{id}/detail")
    public Result<DocumentTemplateResponse> getTemplateWithFields(@Parameter(description = "模板ID") @PathVariable Long id) {
        DocumentTemplateResponse response = documentExportService.getTemplateWithFields(id);
        return Result.success(response);
    }

    @Operation(summary = "根据编码获取模板")
    @GetMapping("/code/{templateCode}")
    public Result<DocumentExportTemplate> getTemplateByCode(
            @Parameter(description = "模板编码") @PathVariable String templateCode) {
        DocumentExportTemplate template = documentExportService.getTemplateByCode(templateCode);
        return Result.success(template);
    }

    @Operation(summary = "获取所有文档导出模板")
    @GetMapping
    public Result<List<DocumentExportTemplate>> getAllTemplates(
            @Parameter(description = "描述模糊查询") @RequestParam(required = false) String description) {
        List<DocumentExportTemplate> templates = documentExportService.getAllTemplates(description);
        return Result.success(templates);
    }

    @Operation(summary = "根据类型获取模板列表")
    @GetMapping("/type/{templateType}")
    public Result<List<DocumentExportTemplate>> getTemplatesByType(
            @Parameter(description = "模板类型: WORD/EXCEL") @PathVariable String templateType) {
        List<DocumentExportTemplate> templates = documentExportService.getTemplatesByType(templateType);
        return Result.success(templates);
    }

    @Operation(summary = "设置默认模板")
    @PostMapping("/{id}/set-default")
    public Result<Void> setDefaultTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Parameter(description = "模板类型") @RequestParam String templateType) {
        Long userId = getCurrentUserId();
        documentExportService.setDefaultTemplate(id, templateType, userId);
        return Result.success();
    }

    @Operation(summary = "上传模板文件")
    @PostMapping("/{id}/upload")
    public Result<String> uploadTemplateFile(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Parameter(description = "模板文件") @RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!".docx".equals(extension) && !".xlsx".equals(extension)) {
                return Result.error("只支持 .docx 和 .xlsx 格式的文件");
            }

            String uploadDir = "uploads/templates/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            Long userId = getCurrentUserId();
            DocumentTemplateUpdateRequest updateRequest = new DocumentTemplateUpdateRequest();
            updateRequest.setId(id);
            updateRequest.setFilePath(filePath.toString());
            documentExportService.updateTemplate(id, updateRequest, userId);

            log.info("模板文件上传成功 - 模板ID: {}, 文件路径: {}", id, filePath);
            return Result.success(filePath.toString());
        } catch (IOException e) {
            log.error("上传模板文件失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传模板图片（签名、印章等）")
    @PostMapping("/{id}/upload-image")
    public Result<Map<String, String>> uploadTemplateImage(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "图片类型（如：signature-签名, seal-印章）") @RequestParam(value = "imageType", required = false, defaultValue = "image") String imageType) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!".jpg".equals(extension) && !".jpeg".equals(extension) && !".png".equals(extension) && !".gif".equals(extension) && !".bmp".equals(extension)) {
                return Result.error("只支持 jpg、jpeg、png、gif、bmp 格式的图片文件");
            }

            String uploadDir = "uploads/template-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = imageType + "_" + id + "_" + UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            String relativePath = filePath.toString().replace("\\", "/");
            
            log.info("模板图片上传成功 - 模板ID: {}, 图片类型: {}, 文件路径: {}", id, imageType, relativePath);
            
            return Result.success(Map.of(
                "filePath", relativePath,
                "fileName", fileName,
                "imageType", imageType
            ));
        } catch (IOException e) {
            log.error("上传模板图片失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "导出Word文档")
    @PostMapping("/{id}/export/word")
    public void exportWord(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @RequestBody DocumentExportRequest request,
            HttpServletResponse response) throws IOException {
        Long userId = getCurrentUserId();
        Map<String, Object> data = request.getData() != null ? request.getData() : Map.of();
        documentExportService.exportWord(id, data, request.getFileName(), response, userId);
    }

    @Operation(summary = "导出 Excel 表格")
    @PostMapping("/{id}/export/excel")
    public void exportExcel(
            @Parameter(description = "模板 ID") @PathVariable Long id,
            @RequestBody DocumentExportRequest request,
            HttpServletResponse response) throws IOException {
        Long userId = getCurrentUserId();
        Map<String, Object> data = request.getData() != null ? request.getData() : Map.of();
        documentExportService.exportExcel(id, data, request.getFileName(), response, userId);
    }

    @Operation(summary = "批量导出 Excel 表格")
    @PostMapping("/batch-export/excel")
    public void batchExportExcel(
            @RequestBody @Valid BatchExportRequest request,
            HttpServletResponse response) throws IOException {
        Long userId = getCurrentUserId();
        documentExportService.batchExportExcel(
            request.getTemplateId(), 
            request.getDataList(), 
            request.getFileName(), 
            request.getOptions(), 
            response, 
            userId
        );
    }

    @Operation(summary = "获取导出历史记录")
    @GetMapping("/export-history")
    public Result<List<DocumentExportHistory>> getExportHistory() {
        Long userId = getCurrentUserId();
        List<DocumentExportHistory> history = documentExportService.getExportHistory(userId);
        return Result.success(history);
    }

    @Operation(summary = "获取指定模板的导出历史")
    @GetMapping("/{id}/export-history")
    public Result<List<DocumentExportHistory>> getExportHistoryByTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        List<DocumentExportHistory> history = documentExportService.getExportHistoryByTemplate(id, userId);
        return Result.success(history);
    }

    @Operation(summary = "获取模板字段配置")
    @GetMapping("/{id}/fields")
    public Result<List<com.lawbackend2.lawbackend2.dto.DocumentTemplateFieldDTO>> getTemplateFields(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        List<com.lawbackend2.lawbackend2.dto.DocumentTemplateFieldDTO> fields = documentExportService.getTemplateFields(id);
        return Result.success(fields);
    }

    @Operation(summary = "预览Word文档模板")
    @GetMapping("/{id}/preview")
    public void previewTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            HttpServletResponse response) throws IOException {
        Long userId = getCurrentUserId();
        documentExportService.previewTemplate(id, response, userId);
    }

    @Operation(summary = "预览Word文档模板(PDF格式)")
    @GetMapping("/{id}/preview/pdf")
    public void previewTemplateAsPdf(
            @Parameter(description = "模板ID") @PathVariable Long id,
            HttpServletResponse response) throws IOException {
        Long userId = getCurrentUserId();
        documentExportService.previewTemplateAsPdf(id, response, userId);
    }

    @Operation(summary = "导出Word文档并转换为PDF")
    @PostMapping("/{id}/export/pdf")
    public void exportAsPdf(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @RequestBody DocumentExportRequest request,
            HttpServletResponse response) throws IOException {
        Long userId = getCurrentUserId();
        Map<String, Object> data = request.getData() != null ? request.getData() : Map.of();
        documentExportService.exportWordAsPdf(id, data, request.getFileName(), response, userId);
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
