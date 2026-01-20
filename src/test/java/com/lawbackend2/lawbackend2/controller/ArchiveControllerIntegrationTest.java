package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.ArchiveCategoryResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveRecordResponse;
import com.lawbackend2.lawbackend2.dto.ArchiveUpdateRequest;
import com.lawbackend2.lawbackend2.dto.ArchiveUploadRequest;
import com.lawbackend2.lawbackend2.entity.ArchiveCategory;
import com.lawbackend2.lawbackend2.entity.ArchiveRecord;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.repository.ArchiveCategoryRepository;
import com.lawbackend2.lawbackend2.repository.ArchiveRecordRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArchiveControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArchiveCategoryRepository archiveCategoryRepository;

    @Autowired
    private ArchiveRecordRepository archiveRecordRepository;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    private ArchiveCategory testCategory;
    private ArchiveRecord testArchiveRecord;
    private FileRecord testFileRecord;

    @BeforeEach
    void setUp() {
        testFileRecord = new FileRecord();
        testFileRecord.setOriginalFileName("test.pdf");
        testFileRecord.setStoredFileName("stored_test.pdf");
        testFileRecord.setFilePath("C:\\law-upload\\test\\test.pdf");
        testFileRecord.setFileSize(1024L);
        testFileRecord.setFileExtension("pdf");
        testFileRecord.setMimeType("application/pdf");
        testFileRecord.setBizType("archive");
        testFileRecord.setBizId(1L);
        testFileRecord.setUploadTime(LocalDateTime.now());
        testFileRecord.setUploadUserId(1L);
        testFileRecord.setFileStatus(1);
        testFileRecord.setStatus("ACTIVE");
        testFileRecord = fileRecordRepository.save(testFileRecord);

        testCategory = new ArchiveCategory();
        testCategory.setCategoryCode("0-1-1");
        testCategory.setCategoryName("1.1 预重整/庭外重组启动文件");
        testCategory.setParentId(null);
        testCategory.setLevel(1);
        testCategory.setSortOrder(0);
        testCategory.setIsRequired(true);
        testCategory.setStatus("ACTIVE");
        testCategory = archiveCategoryRepository.save(testCategory);

        testArchiveRecord = new ArchiveRecord();
        testArchiveRecord.setCaseId(1L);
        testArchiveRecord.setCategoryCode("0-1-1");
        testArchiveRecord.setFileId(testFileRecord.getId());
        testArchiveRecord.setArchiveNo("AH-20260116-0001");
        testArchiveRecord.setFileTitle("测试文件");
        testArchiveRecord.setFileDescription("测试文件描述");
        testArchiveRecord.setUploadUserId(1L);
        testArchiveRecord.setUploadTime(LocalDateTime.now());
        testArchiveRecord.setStatus("ACTIVE");
        testArchiveRecord.setIsConfidential(false);
        testArchiveRecord.setAccessLevel("INTERNAL");
        testArchiveRecord.setVersion(1);
        testArchiveRecord = archiveRecordRepository.save(testArchiveRecord);
    }

    @AfterEach
    void tearDown() {
        archiveRecordRepository.deleteAll();
        archiveCategoryRepository.deleteAll();
        fileRecordRepository.deleteAll();
    }

    @Test
    void testGetCategoryTree_Success() throws Exception {
        mockMvc.perform(get("/archive/categories")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].categoryCode").value("0"))
                .andExpect(jsonPath("$.data[0].categoryName").exists())
                .andExpect(jsonPath("$.data[0].children").isArray());
    }

    @Test
    void testUploadArchiveFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/archive/1/upload")
                        .file(file)
                        .param("categoryCode", "0-1-1")
                        .param("fileTitle", "测试上传文件")
                        .param("fileDescription", "测试文件描述")
                        .param("isConfidential", "false")
                        .param("accessLevel", "INTERNAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.caseId").value(1))
                .andExpect(jsonPath("$.data.categoryCode").value("0-1-1"))
                .andExpect(jsonPath("$.data.archiveNo").exists())
                .andExpect(jsonPath("$.data.fileTitle").value("测试上传文件"));
    }

    @Test
    void testUploadArchiveFile_InvalidFileFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "test content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/archive/1/upload")
                        .file(file)
                        .param("categoryCode", "0-1-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(contains("不支持的文件格式")));
    }

    @Test
    void testUploadArchiveFile_FileTooLarge() throws Exception {
        byte[] largeContent = new byte[50 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                largeContent
        );

        mockMvc.perform(multipart("/archive/1/upload")
                        .file(file)
                        .param("categoryCode", "0-1-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(contains("文件大小不能超过50MB")));
    }

    @Test
    void testUploadArchiveFile_MissingCategoryCode() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/archive/1/upload")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetArchiveFiles_Success() throws Exception {
        mockMvc.perform(get("/archive/1/files")
                        .param("categoryCode", "0-1-1")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetArchiveFiles_WithKeyword() throws Exception {
        mockMvc.perform(get("/archive/1/files")
                        .param("keyword", "测试")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testGetArchiveRecord_Success() throws Exception {
        mockMvc.perform(get("/archive/record/{recordId}", testArchiveRecord.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testArchiveRecord.getId()))
                .andExpect(jsonPath("$.data.caseId").value(1))
                .andExpect(jsonPath("$.data.categoryCode").value("0-1-1"))
                .andExpect(jsonPath("$.data.fileTitle").value("测试文件"));
    }

    @Test
    void testGetArchiveRecord_NotFound() throws Exception {
        mockMvc.perform(get("/archive/record/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(contains("不存在")));
    }

    @Test
    void testUpdateArchiveRecord_Success() throws Exception {
        ArchiveUpdateRequest request = new ArchiveUpdateRequest();
        request.setFileTitle("更新后的文件标题");
        request.setFileDescription("更新后的文件描述");
        request.setIsConfidential(true);
        request.setAccessLevel("CONFIDENTIAL");

        mockMvc.perform(put("/archive/record/{recordId}", testArchiveRecord.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testArchiveRecord.getId()))
                .andExpect(jsonPath("$.data.fileTitle").value("更新后的文件标题"))
                .andExpect(jsonPath("$.data.isConfidential").value(true))
                .andExpect(jsonPath("$.data.accessLevel").value("CONFIDENTIAL"));
    }

    @Test
    void testUpdateArchiveRecord_NotFound() throws Exception {
        ArchiveUpdateRequest request = new ArchiveUpdateRequest();
        request.setFileTitle("更新后的文件标题");

        mockMvc.perform(put("/archive/record/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(contains("不存在")));
    }

    @Test
    void testDeleteArchiveRecord_Success() throws Exception {
        mockMvc.perform(delete("/archive/record/{recordId}", testArchiveRecord.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));

        ArchiveRecord deletedRecord = archiveRecordRepository.findById(testArchiveRecord.getId()).orElse(null);
        if (deletedRecord != null) {
            org.junit.jupiter.api.Assertions.assertEquals("DELETED", deletedRecord.getStatus());
        }
    }

    @Test
    void testDeleteArchiveRecord_NotFound() throws Exception {
        mockMvc.perform(delete("/archive/record/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(contains("不存在")));
    }

    @Test
    void testDeleteArchiveRecords_Success() throws Exception {
        mockMvc.perform(delete("/archive/records/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.List.of(testArchiveRecord.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除成功"));
    }

    @Test
    void testGetArchiveStatistics_Success() throws Exception {
        mockMvc.perform(get("/archive/1/statistics")
                        .param("categoryCode", "0-1-1")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    void testDownloadArchiveFile_Success() throws Exception {
        mockMvc.perform(get("/archive/file/{fileId}/download", testFileRecord.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDownloadArchiveFile_NotFound() throws Exception {
        mockMvc.perform(get("/archive/file/999999/download"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testPreviewArchiveFile_Success() throws Exception {
        mockMvc.perform(get("/archive/file/{fileId}/preview", testFileRecord.getId()))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"));
    }

    @Test
    void testPreviewArchiveFile_NotFound() throws Exception {
        mockMvc.perform(get("/archive/file/999999/preview"))
                .andExpect(status().is5xxServerError());
    }
}
