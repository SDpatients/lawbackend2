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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentExportServiceImpl implements DocumentExportService {

    @Autowired
    private DocumentExportTemplateRepository templateRepository;

    @Autowired
    private DocumentTemplateFieldRepository fieldRepository;

    @Autowired
    private DocumentExportHistoryRepository historyRepository;

    @Override
    @Transactional
    public DocumentExportTemplate createTemplate(DocumentTemplateCreateRequest request, Long userId) {
        if (templateRepository.existsByTemplateCode(request.getTemplateCode())) {
            throw new BusinessException(400, "模板编码 '" + request.getTemplateCode() + "' 已存在，请使用其他编码");
        }

        DocumentExportTemplate template = new DocumentExportTemplate();
        BeanUtils.copyProperties(request, template);
        
        // 处理 config_json 字段：空字符串转为 null，确保是有效的 JSON
        if (!StringUtils.hasText(template.getConfigJson())) {
            template.setConfigJson(null);
        }
        
        template.setCreateUserId(userId);
        template.setUpdateUserId(userId);
        template.setStatus("ACTIVE");
        template.setIsDeleted(false);

        DocumentExportTemplate savedTemplate = templateRepository.save(template);

        // 保存字段映射
        if (request.getFields() != null && !request.getFields().isEmpty()) {
            saveTemplateFields(savedTemplate.getId(), request.getFields(), userId);
        }

        return savedTemplate;
    }

    @Override
    @Transactional
    public DocumentExportTemplate updateTemplate(Long id, DocumentTemplateUpdateRequest request, Long userId) {
        DocumentExportTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "模板不存在，ID: " + id));

        // 先处理字段映射更新
        if (request.getFields() != null) {
            // 使用原生SQL强制删除，绕过Hibernate缓存
            fieldRepository.deleteByTemplateIdNative(id);
            
            // 保存新字段
            saveTemplateFields(id, request.getFields(), userId);
        }

        // 更新模板基本信息
        if (request.getTemplateName() != null) {
            template.setTemplateName(request.getTemplateName());
        }
        if (request.getDescription() != null) {
            template.setDescription(request.getDescription());
        }
        if (request.getConfigJson() != null) {
            // 处理 config_json 字段：空字符串转为 null，确保是有效的 JSON
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

        if (!"WORD".equals(template.getTemplateType())) {
            throw new BusinessException(400, "模板 '" + template.getTemplateName() + "' 的类型是 " + template.getTemplateType() + "，不是Word文档");
        }

        // 获取模板字段配置
        List<DocumentTemplateField> fields = fieldRepository
                .findByTemplateIdAndStatusAndIsDeletedFalseOrderBySortOrderAsc(templateId, "ACTIVE");

        // 准备导出数据
        Map<String, Object> renderData = prepareRenderData(data, fields);

        // 设置响应头
        String exportFileName = StringUtils.hasText(fileName) ? fileName : template.getTemplateName();
        exportFileName = exportFileName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".docx";

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(exportFileName, "UTF-8"));

        try (OutputStream out = response.getOutputStream()) {
            // 如果有模板文件路径，使用模板文件
            if (StringUtils.hasText(template.getFilePath())) {
                File templateFile = new File(template.getFilePath());
                if (templateFile.exists()) {
                    XWPFTemplate xwpfTemplate = XWPFTemplate.compile(templateFile).render(renderData);
                    xwpfTemplate.write(out);
                } else {
                    // 模板文件不存在，创建空白文档
                    createEmptyWordDocument(renderData, out);
                }
            } else {
                // 没有模板文件，创建空白文档
                createEmptyWordDocument(renderData, out);
            }

            out.flush();

            // 记录导出历史
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
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(exportFileName, "UTF-8"));

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
        response.setHeader("Content-Disposition", "inline; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

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
            BeanUtils.copyProperties(dto, field);
            field.setTemplateId(templateId);
            field.setCreateUserId(userId);
            field.setUpdateUserId(userId);
            field.setStatus("ACTIVE");
            field.setIsDeleted(false);
            return field;
        }).collect(Collectors.toList());

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

            // 如果值为空，使用默认值
            if (value == null && StringUtils.hasText(field.getDefaultValue())) {
                value = field.getDefaultValue();
            }

            // 根据字段类型处理值
            if (value != null) {
                switch (field.getFieldType()) {
                    case "DATE":
                        value = formatDateValue(value, field.getFormatPattern());
                        break;
                    case "NUMBER":
                        value = formatNumberValue(value, field.getFormatPattern());
                        break;
                    case "IMAGE":
                        // 图片类型特殊处理
                        if (value instanceof String) {
                            value = Pictures.ofLocal((String) value).create();
                        }
                        break;
                    case "TABLE":
                        // 表格类型特殊处理
                        if (value instanceof List) {
                            value = createTable((List<Map<String, Object>>) value, field);
                        }
                        break;
                    default:
                        // TEXT 和其他类型直接使用字符串
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
}
