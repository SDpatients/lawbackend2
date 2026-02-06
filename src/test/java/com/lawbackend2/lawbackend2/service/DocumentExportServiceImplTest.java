package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.DocumentTemplateCreateRequest;
import com.lawbackend2.lawbackend2.dto.DocumentTemplateFieldDTO;
import com.lawbackend2.lawbackend2.dto.DocumentTemplateUpdateRequest;
import com.lawbackend2.lawbackend2.entity.DocumentExportHistory;
import com.lawbackend2.lawbackend2.entity.DocumentExportTemplate;
import com.lawbackend2.lawbackend2.entity.DocumentTemplateField;
import com.lawbackend2.lawbackend2.repository.DocumentExportHistoryRepository;
import com.lawbackend2.lawbackend2.repository.DocumentExportTemplateRepository;
import com.lawbackend2.lawbackend2.repository.DocumentTemplateFieldRepository;
import com.lawbackend2.lawbackend2.service.impl.DocumentExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocumentExportServiceImplTest {

    @Mock
    private DocumentExportTemplateRepository templateRepository;

    @Mock
    private DocumentTemplateFieldRepository fieldRepository;

    @Mock
    private DocumentExportHistoryRepository historyRepository;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private DocumentExportServiceImpl documentExportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTemplate() {
        // 准备测试数据
        DocumentTemplateCreateRequest request = new DocumentTemplateCreateRequest();
        request.setTemplateName("测试模板");
        request.setTemplateCode("TEST_001");
        request.setTemplateType("WORD");
        request.setDescription("测试描述");
        request.setIsDefault(false);

        List<DocumentTemplateFieldDTO> fields = new ArrayList<>();
        DocumentTemplateFieldDTO field = new DocumentTemplateFieldDTO();
        field.setFieldName("testField");
        field.setFieldLabel("测试字段");
        field.setFieldType("TEXT");
        field.setSortOrder(1);
        fields.add(field);
        request.setFields(fields);

        Long userId = 1L;

        DocumentExportTemplate savedTemplate = new DocumentExportTemplate();
        savedTemplate.setId(1L);
        savedTemplate.setTemplateName("测试模板");
        savedTemplate.setTemplateCode("TEST_001");
        savedTemplate.setTemplateType("WORD");
        savedTemplate.setDescription("测试描述");
        savedTemplate.setIsDefault(false);
        savedTemplate.setStatus("ACTIVE");

        // 模拟方法调用
        when(templateRepository.existsByTemplateCode("TEST_001")).thenReturn(false);
        when(templateRepository.save(any(DocumentExportTemplate.class))).thenReturn(savedTemplate);
        when(fieldRepository.saveAll(any())).thenReturn(new ArrayList<>());

        // 执行测试
        DocumentExportTemplate result = documentExportService.createTemplate(request, userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试模板", result.getTemplateName());
        assertEquals("TEST_001", result.getTemplateCode());
        assertEquals("WORD", result.getTemplateType());
        assertEquals("ACTIVE", result.getStatus());

        verify(templateRepository, times(1)).existsByTemplateCode("TEST_001");
        verify(templateRepository, times(1)).save(any(DocumentExportTemplate.class));
        verify(fieldRepository, times(1)).saveAll(any());
    }

    @Test
    void testCreateTemplate_DuplicateCode() {
        // 准备测试数据
        DocumentTemplateCreateRequest request = new DocumentTemplateCreateRequest();
        request.setTemplateName("测试模板");
        request.setTemplateCode("TEST_001");
        request.setTemplateType("WORD");

        Long userId = 1L;

        // 模拟方法调用
        when(templateRepository.existsByTemplateCode("TEST_001")).thenReturn(true);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            documentExportService.createTemplate(request, userId);
        });

        assertEquals("模板编码已存在", exception.getMessage());
        verify(templateRepository, times(1)).existsByTemplateCode("TEST_001");
        verify(templateRepository, never()).save(any(DocumentExportTemplate.class));
    }

    @Test
    void testUpdateTemplate() {
        // 准备测试数据
        Long templateId = 1L;
        Long userId = 1L;

        DocumentTemplateUpdateRequest request = new DocumentTemplateUpdateRequest();
        request.setId(templateId);
        request.setTemplateName("更新后的模板名称");
        request.setDescription("更新后的描述");

        DocumentExportTemplate existingTemplate = new DocumentExportTemplate();
        existingTemplate.setId(templateId);
        existingTemplate.setTemplateName("原模板名称");
        existingTemplate.setTemplateCode("TEST_001");
        existingTemplate.setTemplateType("WORD");
        existingTemplate.setDescription("原描述");
        existingTemplate.setStatus("ACTIVE");

        DocumentExportTemplate updatedTemplate = new DocumentExportTemplate();
        updatedTemplate.setId(templateId);
        updatedTemplate.setTemplateName("更新后的模板名称");
        updatedTemplate.setTemplateCode("TEST_001");
        updatedTemplate.setTemplateType("WORD");
        updatedTemplate.setDescription("更新后的描述");
        updatedTemplate.setStatus("ACTIVE");

        // 模拟方法调用
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(existingTemplate));
        when(templateRepository.save(any(DocumentExportTemplate.class))).thenReturn(updatedTemplate);
        doNothing().when(fieldRepository).deleteByTemplateId(templateId);

        // 执行测试
        DocumentExportTemplate result = documentExportService.updateTemplate(templateId, request, userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(templateId, result.getId());
        assertEquals("更新后的模板名称", result.getTemplateName());
        assertEquals("更新后的描述", result.getDescription());

        verify(templateRepository, times(1)).findById(templateId);
        verify(templateRepository, times(1)).save(any(DocumentExportTemplate.class));
    }

    @Test
    void testUpdateTemplate_NotFound() {
        // 准备测试数据
        Long templateId = 999L;
        Long userId = 1L;

        DocumentTemplateUpdateRequest request = new DocumentTemplateUpdateRequest();
        request.setId(templateId);
        request.setTemplateName("更新后的模板名称");

        // 模拟方法调用
        when(templateRepository.findById(templateId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            documentExportService.updateTemplate(templateId, request, userId);
        });

        assertEquals("模板不存在", exception.getMessage());
        verify(templateRepository, times(1)).findById(templateId);
        verify(templateRepository, never()).save(any(DocumentExportTemplate.class));
    }

    @Test
    void testDeleteTemplate() {
        // 准备测试数据
        Long templateId = 1L;
        Long userId = 1L;

        DocumentExportTemplate existingTemplate = new DocumentExportTemplate();
        existingTemplate.setId(templateId);
        existingTemplate.setTemplateName("测试模板");
        existingTemplate.setStatus("ACTIVE");
        existingTemplate.setIsDeleted(false);

        DocumentExportTemplate deletedTemplate = new DocumentExportTemplate();
        deletedTemplate.setId(templateId);
        deletedTemplate.setTemplateName("测试模板");
        deletedTemplate.setStatus("DELETED");
        deletedTemplate.setIsDeleted(true);

        // 模拟方法调用
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(existingTemplate));
        when(templateRepository.save(any(DocumentExportTemplate.class))).thenReturn(deletedTemplate);

        // 执行测试
        documentExportService.deleteTemplate(templateId, userId);

        // 验证结果
        verify(templateRepository, times(1)).findById(templateId);
        verify(templateRepository, times(1)).save(any(DocumentExportTemplate.class));
    }

    @Test
    void testGetTemplate() {
        // 准备测试数据
        Long templateId = 1L;

        DocumentExportTemplate template = new DocumentExportTemplate();
        template.setId(templateId);
        template.setTemplateName("测试模板");
        template.setTemplateCode("TEST_001");
        template.setTemplateType("WORD");
        template.setStatus("ACTIVE");

        // 模拟方法调用
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(template));

        // 执行测试
        DocumentExportTemplate result = documentExportService.getTemplate(templateId);

        // 验证结果
        assertNotNull(result);
        assertEquals(templateId, result.getId());
        assertEquals("测试模板", result.getTemplateName());

        verify(templateRepository, times(1)).findById(templateId);
    }

    @Test
    void testGetTemplateByCode() {
        // 准备测试数据
        String templateCode = "TEST_001";

        DocumentExportTemplate template = new DocumentExportTemplate();
        template.setId(1L);
        template.setTemplateName("测试模板");
        template.setTemplateCode(templateCode);
        template.setTemplateType("WORD");
        template.setStatus("ACTIVE");

        // 模拟方法调用
        when(templateRepository.findByTemplateCode(templateCode)).thenReturn(Optional.of(template));

        // 执行测试
        DocumentExportTemplate result = documentExportService.getTemplateByCode(templateCode);

        // 验证结果
        assertNotNull(result);
        assertEquals(templateCode, result.getTemplateCode());

        verify(templateRepository, times(1)).findByTemplateCode(templateCode);
    }

    @Test
    void testGetTemplatesByType() {
        // 准备测试数据
        String templateType = "WORD";

        List<DocumentExportTemplate> templates = new ArrayList<>();
        DocumentExportTemplate template1 = new DocumentExportTemplate();
        template1.setId(1L);
        template1.setTemplateName("模板1");
        template1.setTemplateType("WORD");
        template1.setStatus("ACTIVE");
        templates.add(template1);

        DocumentExportTemplate template2 = new DocumentExportTemplate();
        template2.setId(2L);
        template2.setTemplateName("模板2");
        template2.setTemplateType("WORD");
        template2.setStatus("ACTIVE");
        templates.add(template2);

        // 模拟方法调用
        when(templateRepository.findByTemplateTypeAndStatusAndIsDeletedFalse(templateType, "ACTIVE"))
                .thenReturn(templates);

        // 执行测试
        List<DocumentExportTemplate> result = documentExportService.getTemplatesByType(templateType);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(templateRepository, times(1)).findByTemplateTypeAndStatusAndIsDeletedFalse(templateType, "ACTIVE");
    }

    @Test
    void testGetAllTemplates() {
        // 准备测试数据
        List<DocumentExportTemplate> templates = new ArrayList<>();
        DocumentExportTemplate template1 = new DocumentExportTemplate();
        template1.setId(1L);
        template1.setTemplateName("模板1");
        template1.setStatus("ACTIVE");
        templates.add(template1);

        // 模拟方法调用
        when(templateRepository.findByStatusAndIsDeletedFalseOrderByCreateTimeDesc("ACTIVE"))
                .thenReturn(templates);

        // 执行测试
        List<DocumentExportTemplate> result = documentExportService.getAllTemplates();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(templateRepository, times(1)).findByStatusAndIsDeletedFalseOrderByCreateTimeDesc("ACTIVE");
    }

    @Test
    void testSetDefaultTemplate() {
        // 准备测试数据
        Long templateId = 1L;
        String templateType = "WORD";
        Long userId = 1L;

        List<DocumentExportTemplate> templates = new ArrayList<>();
        DocumentExportTemplate template1 = new DocumentExportTemplate();
        template1.setId(1L);
        template1.setIsDefault(false);
        templates.add(template1);

        DocumentExportTemplate template2 = new DocumentExportTemplate();
        template2.setId(2L);
        template2.setIsDefault(true);
        templates.add(template2);

        DocumentExportTemplate targetTemplate = new DocumentExportTemplate();
        targetTemplate.setId(templateId);
        targetTemplate.setIsDefault(false);

        // 模拟方法调用
        when(templateRepository.findByTemplateTypeAndStatusAndIsDeletedFalse(templateType, "ACTIVE"))
                .thenReturn(templates);
        when(templateRepository.findById(templateId)).thenReturn(Optional.of(targetTemplate));
        when(templateRepository.saveAll(any())).thenReturn(templates);
        when(templateRepository.save(any(DocumentExportTemplate.class))).thenReturn(targetTemplate);

        // 执行测试
        documentExportService.setDefaultTemplate(templateId, templateType, userId);

        // 验证结果
        verify(templateRepository, times(1)).findByTemplateTypeAndStatusAndIsDeletedFalse(templateType, "ACTIVE");
        verify(templateRepository, times(1)).findById(templateId);
        verify(templateRepository, times(1)).saveAll(any());
        verify(templateRepository, times(1)).save(any(DocumentExportTemplate.class));
    }

    @Test
    void testGetExportHistory() {
        // 准备测试数据
        Long userId = 1L;

        List<DocumentExportHistory> histories = new ArrayList<>();
        DocumentExportHistory history1 = new DocumentExportHistory();
        history1.setId(1L);
        history1.setTemplateId(1L);
        history1.setExportType("WORD");
        history1.setExportStatus("SUCCESS");
        histories.add(history1);

        // 模拟方法调用
        when(historyRepository.findByExportedByOrderByExportedTimeDesc(userId)).thenReturn(histories);

        // 执行测试
        List<DocumentExportHistory> result = documentExportService.getExportHistory(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(historyRepository, times(1)).findByExportedByOrderByExportedTimeDesc(userId);
    }

    @Test
    void testGetExportHistoryByTemplate() {
        // 准备测试数据
        Long templateId = 1L;
        Long userId = 1L;

        List<DocumentExportHistory> histories = new ArrayList<>();
        DocumentExportHistory history1 = new DocumentExportHistory();
        history1.setId(1L);
        history1.setTemplateId(templateId);
        history1.setExportType("WORD");
        history1.setExportStatus("SUCCESS");
        histories.add(history1);

        // 模拟方法调用
        when(historyRepository.findByTemplateIdAndExportedByOrderByExportedTimeDesc(templateId, userId))
                .thenReturn(histories);

        // 执行测试
        List<DocumentExportHistory> result = documentExportService.getExportHistoryByTemplate(templateId, userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(historyRepository, times(1)).findByTemplateIdAndExportedByOrderByExportedTimeDesc(templateId, userId);
    }
}
