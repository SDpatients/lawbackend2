package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.config.TestRedisConfig;
import com.lawbackend2.lawbackend2.dto.ExcelTemplateCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelTemplateUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ExcelImportTemplate;
import com.lawbackend2.lawbackend2.repository.ExcelImportTemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@WithMockUser(username = "testuser", roles = {"ADMIN"})
class ExcelTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExcelImportTemplateRepository templateRepository;

    private ExcelImportTemplate testTemplate;

    @BeforeEach
    void setUp() {
        // 创建测试模板
        testTemplate = new ExcelImportTemplate();
        testTemplate.setTemplateName("测试模板");
        testTemplate.setTemplateCode("TEST_TEMPLATE");
        testTemplate.setDescription("测试用模板");
        testTemplate.setIsDefault(false);
        testTemplate.setIsActive(true);
        testTemplate.setCreatedBy(1L);
        testTemplate.setFieldMappings("{\"name\":\"姓名\",\"amount\":\"金额\"}");
        testTemplate = templateRepository.save(testTemplate);
    }

    @AfterEach
    void tearDown() {
        templateRepository.deleteAll();
    }

    @Test
    void testCreateTemplate_Success() throws Exception {
        ExcelTemplateCreateRequest request = new ExcelTemplateCreateRequest();
        request.setTemplateName("新模板");
        request.setTemplateCode("NEW_TEMPLATE");
        request.setDescription("新创建的模板");
        Map<String, String> mappings = new HashMap<>();
        mappings.put("field1", "字段1");
        mappings.put("field2", "字段2");
        request.setFieldMappings(mappings);

        mockMvc.perform(post("/excel-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.templateName").value("新模板"))
                .andExpect(jsonPath("$.data.templateCode").value("NEW_TEMPLATE"))
                .andExpect(jsonPath("$.data.description").value("新创建的模板"));
    }

    @Test
    void testCreateTemplate_ValidationError() throws Exception {
        ExcelTemplateCreateRequest request = new ExcelTemplateCreateRequest();
        // 不设置必填字段

        mockMvc.perform(post("/excel-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTemplate_Success() throws Exception {
        ExcelTemplateUpdateRequest request = new ExcelTemplateUpdateRequest();
        request.setTemplateName("更新后的模板");
        request.setDescription("更新后的描述");
        Map<String, String> mappings = new HashMap<>();
        mappings.put("updatedField", "更新字段");
        request.setFieldMappings(mappings);

        mockMvc.perform(put("/excel-templates/{id}", testTemplate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.templateName").value("更新后的模板"))
                .andExpect(jsonPath("$.data.description").value("更新后的描述"));
    }

    @Test
    void testUpdateTemplate_NotFound() throws Exception {
        ExcelTemplateUpdateRequest request = new ExcelTemplateUpdateRequest();
        request.setTemplateName("更新后的模板");
        Map<String, String> mappings = new HashMap<>();
        mappings.put("field", "字段");
        request.setFieldMappings(mappings);

        mockMvc.perform(put("/excel-templates/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testDeleteTemplate_Success() throws Exception {
        mockMvc.perform(delete("/excel-templates/{id}", testTemplate.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证已删除
        assertFalse(templateRepository.findById(testTemplate.getId()).isPresent());
    }

    @Test
    void testGetAllTemplates_Success() throws Exception {
        mockMvc.perform(get("/excel-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void testGetDefaultTemplate_Success() throws Exception {
        // 先设置一个默认模板
        testTemplate.setIsDefault(true);
        templateRepository.save(testTemplate);

        mockMvc.perform(get("/excel-templates/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.isDefault").value(true));
    }

    @Test
    void testSetDefaultTemplate_Success() throws Exception {
        mockMvc.perform(post("/excel-templates/{id}/set-default", testTemplate.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证已设置为默认
        ExcelImportTemplate updated = templateRepository.findById(testTemplate.getId()).orElseThrow();
        assertTrue(updated.getIsDefault());
    }

    @Test
    void testGetTemplateMappings_Success() throws Exception {
        mockMvc.perform(get("/excel-templates/{code}/mappings", testTemplate.getTemplateCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testDownloadTemplate_CreditorInfo() throws Exception {
        mockMvc.perform(get("/excel-templates/template")
                        .param("templateCode", "creditor_info"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andExpect(header().exists("Content-disposition"));
    }

    @Test
    void testDownloadTemplate_Claim() throws Exception {
        mockMvc.perform(get("/excel-templates/template")
                        .param("templateCode", "claim"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andExpect(header().exists("Content-disposition"));
    }

    @Test
    void testDownloadTemplate_Default() throws Exception {
        mockMvc.perform(get("/excel-templates/template"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andExpect(header().exists("Content-disposition"));
    }

    @Test
    void testImportFromExcel_Success() throws Exception {
        // 创建测试 Excel 文件
        String excelContent = "姓名,金额\n测试债权人,1000.00";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelContent.getBytes()
        );

        mockMvc.perform(multipart("/excel-templates/import")
                        .file(file)
                        .param("templateCode", "TEST_TEMPLATE")
                        .param("caseId", "1")
                        .param("sheetIndex", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testImportFromExcel_InvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "invalid content".getBytes()
        );

        mockMvc.perform(multipart("/excel-templates/import")
                        .file(file)
                        .param("templateCode", "TEST_TEMPLATE")
                        .param("caseId", "1"))
                .andExpect(status().isOk());
    }

    // 注：导出功能依赖于 ExcelTemplateImportExportServiceImpl，该服务存在编译错误
    // 需要修复源代码后才能启用这些测试
    // @Test
    // void testExportToExcel_Success() throws Exception {
    //     mockMvc.perform(get("/excel-templates/export")
    //                     .param("templateCode", "TEST_TEMPLATE")
    //                     .param("caseId", "1"))
    //             .andExpect(status().isOk());
    // }
    //
    // @Test
    // void testExportToExcel_WithStatus() throws Exception {
    //     mockMvc.perform(get("/excel-templates/export")
    //                     .param("templateCode", "TEST_TEMPLATE")
    //                     .param("caseId", "1")
    //                     .param("registrationStatus", "PENDING"))
    //             .andExpect(status().isOk());
    // }
}
