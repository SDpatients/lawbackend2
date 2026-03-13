package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.Pictures;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.Tables;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.DocumentExportHistory;
import com.lawbackend2.lawbackend2.entity.DocumentExportTemplate;
import com.lawbackend2.lawbackend2.entity.DocumentTemplateField;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.DocumentExportHistoryRepository;
import com.lawbackend2.lawbackend2.repository.DocumentExportTemplateRepository;
import com.lawbackend2.lawbackend2.repository.DocumentTemplateFieldRepository;
import com.lawbackend2.lawbackend2.service.DocumentExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

@Slf4j
@Service
public class DocumentExportServiceImpl implements DocumentExportService {

    @Autowired
    private DocumentExportTemplateRepository templateRepository;

    @Autowired
    private DocumentTemplateFieldRepository fieldRepository;

    @Autowired
    private DocumentExportHistoryRepository historyRepository;

    private void setResponseHeader(HttpServletResponse response, String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
    }

    private void setInlineResponseHeader(HttpServletResponse response, String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encodedFileName);
    }

    @Override
    @Transactional
    public DocumentExportTemplate createTemplate(DocumentTemplateCreateRequest request, Long userId) {
        if (templateRepository.existsByTemplateCode(request.getTemplateCode())) {
            throw new BusinessException(400, "模板编码 '" + request.getTemplateCode() + "' 已存在，请使用其他编码");
        }

        DocumentExportTemplate template = new DocumentExportTemplate();
        BeanUtils.copyProperties(request, template);
        
        if (!StringUtils.hasText(template.getConfigJson())) {
            template.setConfigJson(null);
        }
        
        template.setCreateUserId(userId);
        template.setUpdateUserId(userId);
        template.setStatus("ACTIVE");
        template.setIsDeleted(false);

        DocumentExportTemplate savedTemplate = templateRepository.save(template);

        if (request.getFields() != null && !request.getFields().isEmpty()) {
            saveTemplateFields(savedTemplate.getId(), request.getFields(), userId);
        } else if (request.getMappings() != null && !request.getMappings().isEmpty()) {
            saveTemplateMappings(savedTemplate.getId(), request.getMappings(), userId);
        }

        return savedTemplate;
    }

    @Override
    @Transactional
    public DocumentExportTemplate updateTemplate(Long id, DocumentTemplateUpdateRequest request, Long userId) {
        DocumentExportTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "模板不存在，ID: " + id));

        if (request.getFields() != null) {
            fieldRepository.deleteByTemplateIdNative(id);
            saveTemplateFields(id, request.getFields(), userId);
        } else if (request.getMappings() != null) {
            fieldRepository.deleteByTemplateIdNative(id);
            saveTemplateMappings(id, request.getMappings(), userId);
        }

        if (request.getTemplateName() != null) {
            template.setTemplateName(request.getTemplateName());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getFilePath() != null) {
            template.setFilePath(request.getFilePath());
        }
        if (request.getConfigJson() != null) {
            if (StringUtils.hasText(request.getConfigJson())) {
                template.setConfigJson(request.getConfigJson());
            } else {
                template.setConfigJson(null);
            }
        }
        if (request.getIsDefault() != null) {
            template.setIsDefault(request.getIsDefault());
        }
        if (request.getStatus() != null) {
            template.setStatus(request.getStatus());
        }
        template.setUpdateUserId(userId);

        DocumentExportTemplate updatedTemplate = templateRepository.save(template);

        return updatedTemplate;
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id, Long userId) {
        DocumentExportTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "模板不存在，ID: " + id));
        template.setIsDeleted(true);
        template.setStatus("DELETED");
        template.setUpdateUserId(userId);
        templateRepository.save(template);
    }

    @Override
    public DocumentExportTemplate getTemplate(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "模板不存在，ID: " + id));
    }

    @Override
    public DocumentTemplateResponse getTemplateWithFields(Long id) {
        DocumentExportTemplate template = getTemplate(id);
        List<DocumentTemplateFieldDTO> fields = getTemplateFields(id);
        
        DocumentTemplateResponse response = new DocumentTemplateResponse();
        BeanUtils.copyProperties(template, response);
        response.setFields(fields);
        
        List<DocumentTemplateMappingDTO> mappings = fields.stream().map(field -> {
            DocumentTemplateMappingDTO mapping = new DocumentTemplateMappingDTO();
            mapping.setExcelHeader(field.getFieldLabel());
            mapping.setTargetField(field.getFieldName());
            return mapping;
        }).collect(Collectors.toList());
        response.setMappings(mappings);
        
        return response;
    }

    @Override
    public DocumentExportTemplate getTemplateByCode(String templateCode) {
        return templateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new BusinessException(404, "模板不存在，编码: " + templateCode));
    }

    @Override
    public List<DocumentExportTemplate> getTemplatesByType(String templateType) {
        return templateRepository.findByTemplateTypeAndStatusAndIsDeletedFalse(templateType, "ACTIVE");
    }

    @Override
    public List<DocumentExportTemplate> getAllTemplates() {
        return templateRepository.findByStatusAndIsDeletedFalseOrderByCreateTimeDesc("ACTIVE");
    }

    @Override
    public List<DocumentExportTemplate> getAllTemplates(String description) {
        if (description == null || description.trim().isEmpty()) {
            return getAllTemplates();
        }
        return templateRepository.findByStatusAndIsDeletedFalseAndDescriptionContainingOrderByCreateTimeDesc("ACTIVE", description.trim());
    }

    @Override
    @Transactional
    public void setDefaultTemplate(Long id, String templateType, Long userId) {
        // 取消该类型下所有模板的默认状态
        List<DocumentExportTemplate> templates = templateRepository
                .findByTemplateTypeAndStatusAndIsDeletedFalse(templateType, "ACTIVE");
        for (DocumentExportTemplate t : templates) {
            t.setIsDefault(false);
            t.setUpdateUserId(userId);
        }
        templateRepository.saveAll(templates);

        // 设置当前模板为默认
        DocumentExportTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "模板不存在，ID: " + id));
        template.setIsDefault(true);
        template.setUpdateUserId(userId);
        templateRepository.save(template);
    }

    @Override
    public void exportWord(Long templateId, Map<String, Object> data, String fileName, HttpServletResponse response, Long userId) throws IOException {
        long startTime = System.currentTimeMillis();
        DocumentExportTemplate template = getTemplate(templateId);

        log.info("开始Word导出 - 模板ID: {}, 模板名称: {}, 模板类型: {}", templateId, template.getTemplateName(), template.getTemplateType());
        log.info("模板文件路径: {}", template.getFilePath());

        if (!"WORD".equals(template.getTemplateType())) {
            throw new BusinessException(400, "模板 '" + template.getTemplateName() + "' 的类型是 " + template.getTemplateType() + "，不是Word文档");
        }

        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");

        log.info("模板字段数量: {}, 导出数据: {}", fields.size(), data);

        Map<String, Object> renderData = prepareRenderData(data, fields);

        log.info("渲染数据: {}", renderData);

        String exportFileName = StringUtils.hasText(fileName) ? fileName : template.getTemplateName();
        exportFileName = exportFileName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".docx";

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        setResponseHeader(response, exportFileName);

        try (OutputStream out = response.getOutputStream()) {
            if (StringUtils.hasText(template.getFilePath())) {
                File templateFile = new File(template.getFilePath());
                log.info("检查模板文件是否存在: {}, 路径: {}", templateFile.exists(), templateFile.getAbsolutePath());
                if (templateFile.exists()) {
                    log.info("使用模板文件进行渲染");
                    XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(renderData);
                    xwpfTemplate.write(out);
                    xwpfTemplate.close();
                } else {
                    log.warn("模板文件不存在，创建空白文档");
                    createEmptyWordDocument(renderData, out);
                }
            } else {
                log.warn("模板文件路径为空，创建空白文档");
                createEmptyWordDocument(renderData, out);
            }

            out.flush();

            log.info("Word导出成功 - 文件名: {}, 处理时间: {}ms", exportFileName, System.currentTimeMillis() - startTime);
            saveExportHistory(template, exportFileName, (long) 0, "WORD", data, "SUCCESS", null, userId, System.currentTimeMillis() - startTime);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Word导出失败", e);
            saveExportHistory(template, exportFileName, null, "WORD", data, "FAILED", e.getMessage(), userId, System.currentTimeMillis() - startTime);
            throw new BusinessException(500, "Word导出失败: " + e.getMessage());
        }
    }

    @Override
    public void exportExcel(Long templateId, Map<String, Object> data, String fileName, HttpServletResponse response, Long userId) throws IOException {
        long startTime = System.currentTimeMillis();
        DocumentExportTemplate template = getTemplate(templateId);

        if (!"EXCEL".equals(template.getTemplateType())) {
            throw new BusinessException(400, "模板 '" + template.getTemplateName() + "' 的类型是 " + template.getTemplateType() + "，不是Excel表格");
        }

        // 获取模板字段配置
        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");

        // 设置响应头
        String exportFileName = StringUtils.hasText(fileName) ? fileName : template.getTemplateName();
        exportFileName = exportFileName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        setResponseHeader(response, exportFileName);

        try (OutputStream out = response.getOutputStream()) {
            // 如果有模板文件路径，使用模板填充
            if (StringUtils.hasText(template.getFilePath())) {
                File templateFile = new File(template.getFilePath());
                if (templateFile.exists()) {
                    ExcelWriter excelWriter = EasyExcel.write(out)
                            .withTemplate(templateFile)
                            .build();
                    WriteSheet writeSheet = EasyExcel.writerSheet().build();
                    excelWriter.fill(data, writeSheet);
                    excelWriter.finish();
                } else {
                    // 模板文件不存在，创建空白表格
                    createEmptyExcelDocument(data, fields, out);
                }
            } else {
                // 没有模板文件，创建空白表格
                createEmptyExcelDocument(data, fields, out);
            }

            out.flush();

            // 记录导出历史
            saveExportHistory(template, exportFileName, (long) 0, "EXCEL", data, "SUCCESS", null, userId, System.currentTimeMillis() - startTime);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Excel导出失败", e);
            saveExportHistory(template, exportFileName, null, "EXCEL", data, "FAILED", e.getMessage(), userId, System.currentTimeMillis() - startTime);
            throw new BusinessException(500, "Excel导出失败: " + e.getMessage());
        }
    }

    @Override
    public List<DocumentExportHistory> getExportHistory(Long userId) {
        return historyRepository.findByExportedByOrderByExportedTimeDesc(userId);
    }

    @Override
    public List<DocumentExportHistory> getExportHistoryByTemplate(Long templateId, Long userId) {
        return historyRepository.findByTemplateIdAndExportedByOrderByExportedTimeDesc(templateId, userId);
    }

    @Override
    public List<com.lawbackend2.lawbackend2.dto.DocumentTemplateFieldDTO> getTemplateFields(Long templateId) {
        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");
        
        return fields.stream().map(field -> {
            com.lawbackend2.lawbackend2.dto.DocumentTemplateFieldDTO dto = new com.lawbackend2.lawbackend2.dto.DocumentTemplateFieldDTO();
            org.springframework.beans.BeanUtils.copyProperties(field, dto);
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void previewTemplate(Long templateId, javax.servlet.http.HttpServletResponse response, Long userId) throws java.io.IOException {
        long startTime = System.currentTimeMillis();
        DocumentExportTemplate template = getTemplate(templateId);

        if (!"WORD".equals(template.getTemplateType())) {
            throw new BusinessException(400, "模板 '" + template.getTemplateName() + "' 的类型是 " + template.getTemplateType() + "，不支持预览");
        }

        // 获取模板字段配置
        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");

        // 准备预览数据（使用默认值或占位符）
        java.util.Map<String, Object> previewData = new java.util.HashMap<>();
        fields.forEach(field -> {
            String fieldName = field.getFieldName();
            String defaultValue = field.getDefaultValue();
            switch (field.getFieldType()) {
                case "TEXT":
                    previewData.put(fieldName, java.util.Objects.requireNonNullElse(defaultValue, "[文本占位符]"));
                    break;
                case "NUMBER":
                    previewData.put(fieldName, java.util.Objects.requireNonNullElse(defaultValue, "0"));
                    break;
                case "DATE":
                    previewData.put(fieldName, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    break;
                default:
                    previewData.put(fieldName, java.util.Objects.requireNonNullElse(defaultValue, "[占位符]"));
            }
        });

        // 准备渲染数据
        java.util.Map<String, Object> renderData = prepareRenderData(previewData, fields);

        // 设置响应头
        String fileName = template.getTemplateName() + "_预览.docx";
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        setInlineResponseHeader(response, fileName);

        try (java.io.OutputStream out = response.getOutputStream()) {
            // 如果有模板文件路径，使用模板文件
            if (org.springframework.util.StringUtils.hasText(template.getFilePath())) {
                java.io.File templateFile = new java.io.File(template.getFilePath());
                if (templateFile.exists()) {
                    com.deepoove.poi.XWPFTemplate xwpfTemplate = com.deepoove.poi.XWPFTemplate.compile(templateFile).render(renderData);
                    xwpfTemplate.write(out);
                } else {
                    // 模板文件不存在，创建预览文档
                    createEmptyWordDocument(renderData, out);
                }
            } else {
                // 没有模板文件，创建预览文档
                createEmptyWordDocument(renderData, out);
            }

            out.flush();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Word预览失败", e);
            throw new BusinessException(500, "Word预览失败: " + e.getMessage());
        }
    }

    private void saveTemplateFields(Long templateId, List<DocumentTemplateFieldDTO> fieldDTOs, Long userId) {
        List<DocumentTemplateField> fields = fieldDTOs.stream().map(dto -> {
            DocumentTemplateField field = new DocumentTemplateField();
            field.setTemplateId(templateId);
            field.setFieldName(dto.getFieldName());
            field.setFieldLabel(dto.getFieldLabel());
            field.setFieldType(dto.getFieldType());
            field.setSourceField(dto.getSourceField());
            field.setDefaultValue(dto.getDefaultValue());
            field.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
            field.setIsRequired(dto.getIsRequired() != null ? dto.getIsRequired() : false);
            field.setFormatPattern(dto.getFormatPattern());
            field.setCreateUserId(userId);
            field.setUpdateUserId(userId);
            field.setStatus("ACTIVE");
            field.setIsDeleted(false);
            return field;
        }).collect(Collectors.toList());

        fieldRepository.saveAll(fields);
    }

    private void saveTemplateMappings(Long templateId, List<DocumentTemplateMappingDTO> mappings, Long userId) {
        List<DocumentTemplateField> fields = new ArrayList<>();
        for (int i = 0; i < mappings.size(); i++) {
            DocumentTemplateMappingDTO mapping = mappings.get(i);
            DocumentTemplateField field = new DocumentTemplateField();
            field.setTemplateId(templateId);
            field.setFieldName(mapping.getTargetField());
            field.setFieldLabel(mapping.getExcelHeader());
            field.setFieldType("TEXT");
            field.setSourceField(mapping.getTargetField());
            field.setSortOrder(i);
            field.setIsRequired(false);
            field.setCreateUserId(userId);
            field.setUpdateUserId(userId);
            field.setStatus("ACTIVE");
            field.setIsDeleted(false);
            fields.add(field);
        }
        fieldRepository.saveAll(fields);
    }

    private Map<String, Object> prepareRenderData(Map<String, Object> data, List<DocumentTemplateField> fields) {
        Map<String, Object> renderData = new HashMap<>();

        if (data == null) {
            data = new HashMap<>();
        }

        for (DocumentTemplateField field : fields) {
            String fieldName = field.getFieldName();
            Object value = data.get(fieldName);

            if (value == null && StringUtils.hasText(field.getDefaultValue())) {
                value = field.getDefaultValue();
            }

            if (value != null) {
                switch (field.getFieldType()) {
                    case "DATE":
                        value = formatDateValue(value, field.getFormatPattern());
                        break;
                    case "NUMBER":
                        value = formatNumberValue(value, field.getFormatPattern());
                        break;
                    case "IMAGE":
                        if (value instanceof String) {
                            String imagePath = (String) value;
                            File imageFile = new File(imagePath);
                            if (imageFile.exists() && imageFile.isFile()) {
                                try {
                                    value = Pictures.ofLocal(imagePath).create();
                                } catch (Exception e) {
                                    log.warn("图片加载失败，使用文本替代: {} - {}", imagePath, e.getMessage());
                                    value = imagePath;
                                }
                            } else {
                                log.info("图片路径不存在，当作文本处理: {}", imagePath);
                                value = imagePath;
                            }
                        }
                        break;
                    case "TABLE":
                        if (value instanceof List) {
                            value = createTable((List<Map<String, Object>>) value, field);
                        }
                        break;
                    default:
                        value = value.toString();
                }
            }

            renderData.put(fieldName, value != null ? value : "");
        }

        return renderData;
    }

    private Object formatDateValue(Object value, String pattern) {
        if (value instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    StringUtils.hasText(pattern) ? pattern : "yyyy-MM-dd HH:mm:ss"
            );
            return ((LocalDateTime) value).format(formatter);
        }
        return value != null ? value.toString() : "";
    }

    private Object formatNumberValue(Object value, String pattern) {
        // 可以根据pattern格式化数字
        return value != null ? value.toString() : "";
    }

    private Object createTable(List<Map<String, Object>> tableData, DocumentTemplateField field) {
        if (tableData == null || tableData.isEmpty()) {
            return Tables.create();
        }

        // 创建表头
        RowRenderData headerRow = Rows.create("列1", "列2", "列3");

        // 创建数据行
        List<RowRenderData> dataRows = tableData.stream()
                .map(row -> Rows.create(
                        row.getOrDefault("col1", "").toString(),
                        row.getOrDefault("col2", "").toString(),
                        row.getOrDefault("col3", "").toString()
                ))
                .collect(Collectors.toList());

        // 合并表头和数据行
        List<RowRenderData> allRows = new ArrayList<>();
        allRows.add(headerRow);
        allRows.addAll(dataRows);

        return Tables.create(allRows.toArray(new RowRenderData[0]));
    }

    private void createEmptyWordDocument(Map<String, Object> data, OutputStream out) throws IOException {
        // 创建一个包含数据的Word文档
        // 使用Apache POI创建简单的Word文档
        org.apache.poi.xwpf.usermodel.XWPFDocument document = new org.apache.poi.xwpf.usermodel.XWPFDocument();
        
        // 添加标题
        org.apache.poi.xwpf.usermodel.XWPFParagraph titlePara = document.createParagraph();
        org.apache.poi.xwpf.usermodel.XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("文档导出结果");
        titleRun.setFontSize(16);
        titleRun.setBold(true);
        titleRun.addBreak();
        titleRun.addBreak();
        
        // 添加字段数据
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                org.apache.poi.xwpf.usermodel.XWPFParagraph para = document.createParagraph();
                org.apache.poi.xwpf.usermodel.XWPFRun run = para.createRun();
                run.setText(entry.getKey() + ": " + entry.getValue());
                run.setFontSize(12);
            }
        } else {
            org.apache.poi.xwpf.usermodel.XWPFParagraph para = document.createParagraph();
            org.apache.poi.xwpf.usermodel.XWPFRun run = para.createRun();
            run.setText("无数据");
            run.setFontSize(12);
        }
        
        // 写入输出流
        document.write(out);
        document.close();
    }

    private byte[] createEmptyDocxBytes() {
        // 不再需要这个方法
        return new byte[0];
    }

    private void createEmptyExcelDocument(Map<String, Object> data, List<DocumentTemplateField> fields, OutputStream out) {
        // 创建简单的Excel表格
        List<List<String>> tableData = new ArrayList<>();

        // 表头
        List<String> headers = fields.stream()
                .map(DocumentTemplateField::getFieldLabel)
                .collect(Collectors.toList());
        tableData.add(headers);

        // 数据行
        List<String> rowData = fields.stream()
                .map(field -> {
                    Object value = data.get(field.getFieldName());
                    return value != null ? value.toString() : "";
                })
                .collect(Collectors.toList());
        tableData.add(rowData);

        EasyExcel.write(out).sheet("Sheet1").doWrite(tableData);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void saveExportHistory(DocumentExportTemplate template, String fileName, Long fileSize,
                                   String exportType, Map<String, Object> params, String status,
                                   String errorMessage, Long userId, Long processingTime) {
        try {
            DocumentExportHistory history = new DocumentExportHistory();
            history.setTemplateId(template.getId());
            history.setExportType(exportType);
            history.setFileName(fileName);
            history.setFileSize(fileSize);
            history.setExportParams(params != null ? objectMapper.writeValueAsString(params) : null);
            history.setExportStatus(status);
            history.setErrorMessage(errorMessage);
            history.setExportedBy(userId);
            history.setExportedTime(LocalDateTime.now());
            history.setProcessingTime(processingTime != null ? processingTime.intValue() : null);
            history.setCreateUserId(userId);
            history.setUpdateUserId(userId);
            history.setStatus("ACTIVE");
            history.setIsDeleted(false);

            historyRepository.save(history);
        } catch (Exception e) {
            log.error("保存导出历史失败", e);
        }
    }

    @Override
    public void previewTemplateAsPdf(Long templateId, HttpServletResponse response, Long userId) throws IOException {
        long startTime = System.currentTimeMillis();
        DocumentExportTemplate template = getTemplate(templateId);

        if (!"WORD".equals(template.getTemplateType())) {
            throw new BusinessException(400, "模板 '" + template.getTemplateName() + "' 的类型是 " + template.getTemplateType() + "，不支持PDF预览");
        }

        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");

        Map<String, Object> previewData = new HashMap<>();
        fields.forEach(field -> {
            String fieldName = field.getFieldName();
            String defaultValue = field.getDefaultValue();
            switch (field.getFieldType()) {
                case "TEXT":
                    previewData.put(fieldName, java.util.Objects.requireNonNullElse(defaultValue, "[文本占位符]"));
                    break;
                case "NUMBER":
                    previewData.put(fieldName, java.util.Objects.requireNonNullElse(defaultValue, "0"));
                    break;
                case "DATE":
                    previewData.put(fieldName, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    break;
                default:
                    previewData.put(fieldName, java.util.Objects.requireNonNullElse(defaultValue, "[占位符]"));
            }
        });

        Map<String, Object> renderData = prepareRenderData(previewData, fields);

        String fileName = template.getTemplateName() + "_预览.pdf";
        response.setContentType("application/pdf");
        setInlineResponseHeader(response, fileName);

        try (ByteArrayOutputStream wordOut = new ByteArrayOutputStream();
             OutputStream pdfOut = response.getOutputStream()) {

            if (StringUtils.hasText(template.getFilePath())) {
                File templateFile = new File(template.getFilePath());
                if (templateFile.exists()) {
                    XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(renderData);
                    xwpfTemplate.write(wordOut);
                    xwpfTemplate.close();
                } else {
                    createEmptyWordDocument(renderData, wordOut);
                }
            } else {
                createEmptyWordDocument(renderData, wordOut);
            }

            byte[] wordBytes = wordOut.toByteArray();
            byte[] pdfBytes = convertWordToPdf(wordBytes);

            pdfOut.write(pdfBytes);
            pdfOut.flush();

            log.info("PDF预览成功 - 模板ID: {}, 处理时间: {}ms", templateId, System.currentTimeMillis() - startTime);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("PDF预览失败", e);
            throw new BusinessException(500, "PDF预览失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] convertWordToPdf(byte[] wordBytes) throws IOException {
        try (ByteArrayInputStream wordIn = new ByteArrayInputStream(wordBytes);
             ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {

            IConverter converter = LocalConverter.builder().build();

            boolean success = converter.convert(wordIn)
                    .as(DocumentType.DOCX)
                    .to(pdfOut)
                    .as(DocumentType.PDF)
                    .execute();

            converter.shutDown();

            if (!success) {
                throw new BusinessException(500, "Word转PDF转换失败");
            }

            return pdfOut.toByteArray();
        } catch (Exception e) {
            log.error("Word转PDF失败", e);
            throw new BusinessException(500, "Word转PDF失败: " + e.getMessage() + "。请确保服务器已安装Microsoft Word或使用在线预览服务。");
        }
    }

    @Override
    public void exportWordAsPdf(Long templateId, Map<String, Object> data, String fileName, HttpServletResponse response, Long userId) throws IOException {
        long startTime = System.currentTimeMillis();
        DocumentExportTemplate template = getTemplate(templateId);

        log.info("开始PDF导出 - 模板ID: {}, 模板名称: {}", templateId, template.getTemplateName());

        if (!"WORD".equals(template.getTemplateType())) {
            throw new BusinessException(400, "模板 '" + template.getTemplateName() + "' 的类型是 " + template.getTemplateType() + "，不支持PDF导出");
        }

        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");

        Map<String, Object> renderData = prepareRenderData(data, fields);

        String exportFileName = StringUtils.hasText(fileName) ? fileName : template.getTemplateName();
        exportFileName = exportFileName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".pdf";

        response.setContentType("application/pdf");
        setResponseHeader(response, exportFileName);

        try (ByteArrayOutputStream wordOut = new ByteArrayOutputStream();
             OutputStream pdfOut = response.getOutputStream()) {

            if (StringUtils.hasText(template.getFilePath())) {
                File templateFile = new File(template.getFilePath());
                if (templateFile.exists()) {
                    XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(renderData);
                    xwpfTemplate.write(wordOut);
                    xwpfTemplate.close();
                } else {
                    createEmptyWordDocument(renderData, wordOut);
                }
            } else {
                createEmptyWordDocument(renderData, wordOut);
            }

            byte[] wordBytes = wordOut.toByteArray();
            byte[] pdfBytes = convertWordToPdf(wordBytes);

            pdfOut.write(pdfBytes);
            pdfOut.flush();

            log.info("PDF导出成功 - 文件名: {}, 处理时间: {}ms", exportFileName, System.currentTimeMillis() - startTime);
            saveExportHistory(template, exportFileName, (long) pdfBytes.length, "PDF", data, "SUCCESS", null, userId, System.currentTimeMillis() - startTime);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("PDF导出失败", e);
            saveExportHistory(template, fileName, null, "PDF", data, "FAILED", e.getMessage(), userId, System.currentTimeMillis() - startTime);
            throw new BusinessException(500, "PDF 导出失败：" + e.getMessage());
        }
    }

    @Override
    public void batchExportExcel(Long templateId, List<Map<String, Object>> dataList, String fileName, 
                                 BatchExportRequest.ExportOptions options, HttpServletResponse response, Long userId) throws IOException {
        long startTime = System.currentTimeMillis();
        DocumentExportTemplate template = getTemplate(templateId);

        log.info("开始 Excel 批量导出 - 模板 ID: {}, 模板名称：{}, 数据条数：{}", templateId, template.getTemplateName(), dataList.size());

        if (!"EXCEL".equals(template.getTemplateType())) {
            throw new BusinessException(400, "模板 '" + template.getTemplateName() + "' 的类型是 " + template.getTemplateType() + "，不是 Excel 表格");
        }

        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");
        
        log.info("模板字段数量：{}", fields.size());
        for (DocumentTemplateField field : fields) {
            log.info("字段映射 - fieldLabel: {}, fieldName: {}", field.getFieldLabel(), field.getFieldName());
        }

        Map<String, String> headerToFieldMap = new HashMap<>();
        Map<String, String> fieldToHeaderMap = new HashMap<>();
        for (DocumentTemplateField field : fields) {
            if (field.getFieldLabel() != null && field.getFieldName() != null) {
                headerToFieldMap.put(field.getFieldLabel(), field.getFieldName());
                fieldToHeaderMap.put(field.getFieldName(), field.getFieldLabel());
            }
        }
        
        log.info("headerToFieldMap: {}", headerToFieldMap);

        String exportFileName = StringUtils.hasText(fileName) ? fileName : template.getTemplateName();
        exportFileName = exportFileName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        setResponseHeader(response, exportFileName);

        try (OutputStream out = response.getOutputStream()) {
            org.apache.poi.ss.usermodel.Workbook workbook;

            if (StringUtils.hasText(template.getFilePath())) {
                File templateFile = new File(template.getFilePath());
                log.info("模板文件路径：{}, 文件是否存在：{}", template.getFilePath(), templateFile.exists());
                if (templateFile.exists()) {
                    workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(new FileInputStream(templateFile));
                } else {
                    log.warn("模板文件不存在，创建空白工作簿");
                    workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                }
            } else {
                log.warn("模板文件路径为空，创建空白工作簿");
                workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            }

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            log.info("原始 Sheet 名称：{}", sheet.getSheetName());
            
            if (options != null && options.getSheetName() != null) {
                String sheetName = options.getSheetName();
                log.info("使用指定的 Sheet 名称：{}", sheetName);
                org.apache.poi.ss.usermodel.Sheet existingSheet = workbook.getSheet(sheetName);
                if (existingSheet != null) {
                    sheet = existingSheet;
                    log.info("找到指定的 Sheet：{}", sheetName);
                } else {
                    log.warn("未找到指定的 Sheet '{}'，使用原始 Sheet: {}", sheetName, sheet.getSheetName());
                }
            }

            int startRow = (options != null && options.getStartRow() != null) ? options.getStartRow() : 2;
            int headerRowNum = (options != null && options.getHeaderRow() != null) ? options.getHeaderRow() : 1;
            Boolean shiftRows = (options != null && options.getShiftRows() != null) ? options.getShiftRows() : false;
            log.info("startRow: {}, headerRowNum: {}, shiftRows: {}", startRow, headerRowNum, shiftRows);

            org.apache.poi.ss.usermodel.Row headerRow = sheet.getRow(headerRowNum - 1);
            if (headerRow == null) {
                log.error("模板文件的第 {} 行为空！请确保模板文件的第 {} 行包含表头", headerRowNum, headerRowNum);
                throw new BusinessException(500, "模板文件格式错误：第 " + headerRowNum + " 行应为表头行");
            }
            
            log.info("表头单元格数量：{}", headerRow.getLastCellNum());
            
            if (headerRow.getLastCellNum() <= 0) {
                log.error("模板文件的表头单元格数量为 0！请确保模板文件的第 {} 行包含表头", headerRowNum);
                throw new BusinessException(500, "模板文件格式错误：表头行没有单元格");
            }
            
            // 如果需要移动行，先计算需要插入的行数
            if (shiftRows && dataList != null) {
                int dataRowCount = dataList.size();
                int existingRows = sheet.getLastRowNum() - (headerRowNum - 1);
                
                if (dataRowCount > existingRows) {
                    int rowsToInsert = dataRowCount - existingRows;
                    log.info("数据行数 ({}) 超过模板行数 ({}), 需要插入 {} 行", dataRowCount, existingRows, rowsToInsert);
                    
                    // 从 startRow 开始向下移动现有行
                    sheet.shiftRows(startRow - 1 + dataRowCount, sheet.getLastRowNum(), rowsToInsert);
                }
            }

            Map<Integer, String> cellFieldMap = new HashMap<>();
            Map<Integer, String> cellHeaderMap = new HashMap<>();
            for (int cellNum = 0; cellNum < headerRow.getLastCellNum(); cellNum++) {
                org.apache.poi.ss.usermodel.Cell headerCell = headerRow.getCell(cellNum);
                if (headerCell != null) {
                    String headerValue = getCellValueAsString(headerCell);
                    log.info("第 {} 列表头原始值：'{}'", cellNum, headerValue);
                    
                    if (headerValue != null && !headerValue.trim().isEmpty()) {
                        String trimmedHeader = headerValue.trim();
                        String fieldName = null;
                        
                        if (trimmedHeader.startsWith("{{.") && trimmedHeader.endsWith("}}")) {
                            String placeholderName = trimmedHeader.substring(3, trimmedHeader.length() - 2);
                            log.info("检测到占位符格式，提取名称：{}", placeholderName);
                            
                            if (headerToFieldMap.containsKey(placeholderName)) {
                                fieldName = headerToFieldMap.get(placeholderName);
                                log.info("通过 mappings 映射：{} -> {}", placeholderName, fieldName);
                            } else {
                                fieldName = placeholderName;
                                log.info("mappings 中未找到，直接使用：{}", fieldName);
                            }
                            
                            headerCell.setCellValue(placeholderName);
                        } else {
                            if (headerToFieldMap.containsKey(trimmedHeader)) {
                                fieldName = headerToFieldMap.get(trimmedHeader);
                                log.info("直接匹配表头，通过 mappings 映射：{} -> {}", trimmedHeader, fieldName);
                            } else {
                                fieldName = trimmedHeader;
                                log.info("直接匹配表头，无映射：{}", fieldName);
                            }
                        }
                        
                        cellFieldMap.put(cellNum, fieldName);
                        cellHeaderMap.put(cellNum, trimmedHeader);
                    }
                }
            }
            
            log.info("cellFieldMap: {}", cellFieldMap);
            log.info("dataList 第一条数据：{}", dataList.isEmpty() ? "无数据" : dataList.get(0));

            int rowNum = startRow - 1;
            int dataIndex = 0;
            for (Map<String, Object> rowData : dataList) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                
                for (Map.Entry<Integer, String> entry : cellFieldMap.entrySet()) {
                    Integer cellNum = entry.getKey();
                    String fieldName = entry.getValue();
                    String headerName = cellHeaderMap.get(cellNum);
                    
                    Object value = findFieldValue(rowData, fieldName);
                    
                    if (dataIndex == 0) {
                        log.info("第一行数据 - 列 {}，fieldName: {}, headerName: {}, 查找结果：{}", cellNum, fieldName, headerName, value);
                    }
                    
                    if (value == null && !fieldName.equals(headerName)) {
                        value = findFieldValue(rowData, headerName);
                        if (dataIndex == 0) {
                            log.info("第一行数据 - 列 {}，用 headerName 再次查找：{}, 结果：{}", cellNum, headerName, value);
                        }
                    }
                    
                    if (value != null) {
                        org.apache.poi.ss.usermodel.Cell dataCell = row.createCell(cellNum);
                        setCellValue(dataCell, value);
                    }
                }
                dataIndex++;
            }

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            workbook.close();

            log.info("Excel 批量导出成功 - 文件名：{}, 数据条数：{}, 处理时间：{}ms", exportFileName, dataList.size(), System.currentTimeMillis() - startTime);
            saveExportHistory(template, exportFileName, (long) 0, "EXCEL_BATCH", Map.of("dataList", dataList), "SUCCESS", null, userId, System.currentTimeMillis() - startTime);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Excel 批量导出失败", e);
            saveExportHistory(template, exportFileName, null, "EXCEL_BATCH", Map.of("dataList", dataList), "FAILED", e.getMessage(), userId, System.currentTimeMillis() - startTime);
            throw new BusinessException(500, "Excel 批量导出失败：" + e.getMessage());
        }
    }

    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private Object findFieldValue(Map<String, Object> rowData, String fieldName) {
        if (rowData.containsKey(fieldName)) {
            return rowData.get(fieldName);
        }
        String trimmedName = fieldName.trim();
        if (rowData.containsKey(trimmedName)) {
            return rowData.get(trimmedName);
        }
        return null;
    }

    private void setCellValue(org.apache.poi.ss.usermodel.Cell cell, Object value) {
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof java.util.Date) {
            cell.setCellValue((java.util.Date) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((LocalDateTime) value));
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
