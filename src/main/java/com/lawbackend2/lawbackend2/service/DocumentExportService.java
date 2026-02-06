package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.DocumentExportHistory;
import com.lawbackend2.lawbackend2.entity.DocumentExportTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DocumentExportService {

    DocumentExportTemplate createTemplate(DocumentTemplateCreateRequest request, Long userId);

    DocumentExportTemplate updateTemplate(Long id, DocumentTemplateUpdateRequest request, Long userId);

    void deleteTemplate(Long id, Long userId);

    DocumentExportTemplate getTemplate(Long id);

    DocumentExportTemplate getTemplateByCode(String templateCode);

    List<DocumentExportTemplate> getTemplatesByType(String templateType);

    List<DocumentExportTemplate> getAllTemplates();

    void setDefaultTemplate(Long id, String templateType, Long userId);

    void exportWord(Long templateId, Map<String, Object> data, String fileName, HttpServletResponse response, Long userId) throws IOException;

    void exportExcel(Long templateId, Map<String, Object> data, String fileName, HttpServletResponse response, Long userId) throws IOException;

    List<DocumentExportHistory> getExportHistory(Long userId);

    List<DocumentExportHistory> getExportHistoryByTemplate(Long templateId, Long userId);

    List<com.lawbackend2.lawbackend2.dto.DocumentTemplateFieldDTO> getTemplateFields(Long templateId);

    void previewTemplate(Long templateId, javax.servlet.http.HttpServletResponse response, Long userId) throws java.io.IOException;
}
