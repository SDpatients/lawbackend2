package com.lawbackend2.lawbackend2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.dto.request.BatchUpdateStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CaseTaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BankruptCaseRepository bankruptCaseRepository;

    @Autowired
    private CaseTaskRepository caseTaskRepository;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    private BankruptCase testCase;
    private CaseTask testTask;

    @BeforeEach
    void setUp() {
        testCase = new BankruptCase();
        testCase.setCaseNumber("TEST001");
        testCase.setCaseName("测试案件");
        testCase.setAcceptanceDate(LocalDate.now());
        testCase.setCaseStatus("PENDING");
        testCase = bankruptCaseRepository.save(testCase);

        testTask = new CaseTask();
        testTask.setCaseId(testCase.getId());
        testTask.setTaskCode("TASK_001");
        testTask.setTaskName("提交破产申请材料");
        testTask.setTaskDescription("申请人");
        testTask.setStatus("IN_PROGRESS");
        testTask.setSortOrder(1);
        testTask = caseTaskRepository.save(testTask);
    }

    @AfterEach
    void tearDown() {
        fileRecordRepository.deleteAll();
        caseTaskRepository.deleteAll();
        bankruptCaseRepository.deleteAll();
    }

    @Test
    void testGetTasks_Success() throws Exception {
        mockMvc.perform(get("/api/case-tasks")
                        .param("caseId", testCase.getId().toString())
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].taskCode").value("TASK_001"))
                .andExpect(jsonPath("$.data.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void testGetTasks_WithStatus() throws Exception {
        mockMvc.perform(get("/api/case-tasks")
                        .param("caseId", testCase.getId().toString())
                        .param("status", "IN_PROGRESS")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetTasks_InvalidCaseId() throws Exception {
        mockMvc.perform(get("/api/case-tasks")
                        .param("caseId", "999")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        mockMvc.perform(get("/api/case-tasks/{id}", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testTask.getId()))
                .andExpect(jsonPath("$.data.taskCode").value("TASK_001"))
                .andExpect(jsonPath("$.data.caseNumber").value("TEST001"));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        mockMvc.perform(get("/api/case-tasks/{id}", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("案件任务不存在")));
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        CaseTaskUpdateRequest request = new CaseTaskUpdateRequest();
        request.setTaskDescription("更新后的描述");
        request.setStatus("COMPLETED");

        mockMvc.perform(patch("/api/case-tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.taskDescription").value("更新后的描述"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void testUpdateTask_InvalidStatus() throws Exception {
        CaseTaskUpdateRequest request = new CaseTaskUpdateRequest();
        request.setStatus("INVALID_STATUS");

        mockMvc.perform(patch("/api/case-tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("无效的任务状态")));
    }

    @Test
    void testBatchUpdateStatus_Success() throws Exception {
        BatchUpdateStatusRequest request = new BatchUpdateStatusRequest();
        request.setTaskIds(java.util.Arrays.asList(testTask.getId()));
        request.setStatus("COMPLETED");

        mockMvc.perform(put("/api/case-tasks/batch-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failCount").value(0));
    }

    @Test
    void testGetStatistics_Success() throws Exception {
        mockMvc.perform(get("/api/case-tasks/statistics/{caseId}", testCase.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalTasks").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.completedTasks").isNumber())
                .andExpect(jsonPath("$.data.inProgressTasks").isNumber())
                .andExpect(jsonPath("$.data.completionRate").isNumber());
    }

    @Test
    void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .multipart("/api/case-tasks/{taskId}/files", testTask.getId())
                        .file(file)
                        .param("description", "文件描述"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.originalFileName").value("test.pdf"))
                .andExpect(jsonPath("$.data.fileSize").value(12));
    }

    @Test
    void testUploadFile_TaskNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .multipart("/api/case-tasks/{taskId}/files", 999)
                        .file(file)
                        .param("description", "文件描述"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("案件任务不存在")));
    }

    @Test
    void testGetFiles_Success() throws Exception {
        FileRecord file = new FileRecord();
        file.setOriginalFileName("test.pdf");
        file.setFilePath("/test/path/test.pdf");
        file.setFileSize(1024L);
        file.setBizType("CASE_TASK");
        file.setBizId(testTask.getId().toString());
        file.setUploadTime(java.time.LocalDateTime.now());
        file.setUploadUserId(1L);
        file.setFileStatus(1);
        file.setStatus("ACTIVE");
        fileRecordRepository.save(file);

        mockMvc.perform(get("/api/case-tasks/{taskId}/files", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].originalFileName").value("test.pdf"));
    }

    @Test
    void testDeleteFile_Success() throws Exception {
        FileRecord file = new FileRecord();
        file.setOriginalFileName("test.pdf");
        file.setFilePath("/test/path/test.pdf");
        file.setFileSize(1024L);
        file.setBizType("CASE_TASK");
        file.setBizId(testTask.getId().toString());
        file.setUploadTime(java.time.LocalDateTime.now());
        file.setUploadUserId(1L);
        file.setFileStatus(1);
        file.setStatus("ACTIVE");
        file = fileRecordRepository.save(file);

        mockMvc.perform(delete("/api/case-tasks/{taskId}/files/{fileId}", testTask.getId(), file.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteFile_TaskNotFound() throws Exception {
        mockMvc.perform(delete("/api/case-tasks/{taskId}/files/{fileId}", 999, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("案件任务不存在")));
    }

    @Test
    void testDeleteFile_FileNotFound() throws Exception {
        mockMvc.perform(delete("/api/case-tasks/{taskId}/files/{fileId}", testTask.getId(), 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("文件不存在")));
    }
}
