package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.entity.FileRecord;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    private FileRecord testFile;

    @BeforeEach
    void setUp() {
        testFile = new FileRecord();
        testFile.setOriginalFileName("test.txt");
        testFile.setStoredFileName("stored_test.txt");
        testFile.setFilePath("C:\\law-upload\\test\\test.txt");
        testFile.setFileSize(1024L);
        testFile.setFileExtension("txt");
        testFile.setMimeType("text/plain");
        testFile.setBizType("test");
        testFile.setBizId(1L);
        testFile.setFileStatus(1);
        testFile.setStatus("ACTIVE");
        testFile = fileRecordRepository.save(testFile);
    }

    @AfterEach
    void tearDown() {
        fileRecordRepository.deleteAll();
    }

    @Test
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/file/upload")
                        .file(file)
                        .param("bizType", "test")
                        .param("bizId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fileId").isNumber())
                .andExpect(jsonPath("$.data.originalFileName").value("test.txt"));
    }

    @Test
    void testGetFileInfo_Success() throws Exception {
        mockMvc.perform(get("/file/{fileId}", testFile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testFile.getId()))
                .andExpect(jsonPath("$.data.originalFileName").value("test.txt"));
    }

    @Test
    void testGetFileList_Success() throws Exception {
        mockMvc.perform(get("/file/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        mockMvc.perform(delete("/file/{fileId}", testFile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
