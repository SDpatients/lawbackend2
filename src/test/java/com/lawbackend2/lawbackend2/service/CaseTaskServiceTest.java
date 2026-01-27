package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CaseTaskUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskStatistics;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.enums.CaseTaskStatus;
import com.lawbackend2.lawbackend2.exception.CaseTaskNotFoundException;
import com.lawbackend2.lawbackend2.exception.CaseTaskStatusInvalidException;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.impl.CaseTaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseTaskServiceTest {

    @Mock
    private CaseTaskRepository caseTaskRepository;

    @Mock
    private FileRecordRepository fileRecordRepository;

    @InjectMocks
    private CaseTaskServiceImpl caseTaskService;

    private CaseTask mockTask;
    private FileRecord mockFile;

    @BeforeEach
    void setUp() {
        mockTask = new CaseTask();
        mockTask.setId(1L);
        mockTask.setCaseId(1L);
        mockTask.setTaskCode("TASK_001");
        mockTask.setTaskName("提交破产申请材料");
        mockTask.setTaskDescription("申请人");
        mockTask.setStatus(CaseTaskStatus.IN_PROGRESS.name());
        mockTask.setSortOrder(1);

        mockFile = new FileRecord();
        mockFile.setId(1L);
        mockFile.setOriginalFileName("test.pdf");
        mockFile.setFileSize(1024L);
    }

    @Test
    void testCreateTasksForCase_Success() {
        when(caseTaskRepository.saveAll(any(List.class))).thenAnswer(invocation -> {
            List<CaseTask> tasks = invocation.getArgument(0);
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).setId((long) (i + 1));
            }
            return tasks;
        });

        List<CaseTask> result = caseTaskService.createTasksForCase(1L);

        assertNotNull(result);
        assertEquals(23, result.size());
        assertEquals(1L, result.get(0).getCaseId());
        assertEquals("TASK_001", result.get(0).getTaskCode());
        assertEquals("TASK_023", result.get(22).getTaskCode());
        assertEquals(CaseTaskStatus.IN_PROGRESS.name(), result.get(0).getStatus());
        verify(caseTaskRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    void testGetTasksByCaseId_Success() {
        List<CaseTask> tasks = Arrays.asList(mockTask);
        Page<CaseTask> page = new PageImpl<>(tasks);
        when(caseTaskRepository.findByCaseId(eq(1L), any(PageRequest.class))).thenReturn(page);

        Page<CaseTaskResponse> result = caseTaskService.getTasksByCaseId(1L, null, null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("TASK_001", result.getContent().get(0).getTaskCode());
        verify(caseTaskRepository, times(1)).findByCaseId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetTasksByCaseId_WithStatus() {
        List<CaseTask> tasks = Arrays.asList(mockTask);
        when(caseTaskRepository.findByCaseIdAndStatus(1L, CaseTaskStatus.IN_PROGRESS.name())).thenReturn(tasks);

        Page<CaseTaskResponse> result = caseTaskService.getTasksByCaseId(1L, CaseTaskStatus.IN_PROGRESS.name(), null, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(caseTaskRepository, times(1)).findByCaseIdAndStatus(1L, CaseTaskStatus.IN_PROGRESS.name());
    }

    @Test
    void testGetTaskById_Success() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        CaseTask result = caseTaskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TASK_001", result.getTaskCode());
        verify(caseTaskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        when(caseTaskRepository.findById(999L)).thenReturn(Optional.empty());

        CaseTaskNotFoundException exception = assertThrows(CaseTaskNotFoundException.class, () -> {
            caseTaskService.getTaskById(999L);
        });

        assertTrue(exception.getMessage().contains("案件任务不存在: 999"));
        verify(caseTaskRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateTask_Success() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(caseTaskRepository.save(any(CaseTask.class))).thenReturn(mockTask);

        CaseTaskUpdateRequest request = new CaseTaskUpdateRequest();
        request.setTaskDescription("更新后的描述");
        request.setStatus(CaseTaskStatus.COMPLETED.name());

        CaseTaskResponse result = caseTaskService.updateTask(1L, request);

        assertNotNull(result);
        assertEquals("更新后的描述", mockTask.getTaskDescription());
        assertEquals(CaseTaskStatus.COMPLETED.name(), mockTask.getStatus());
        verify(caseTaskRepository, times(1)).save(any(CaseTask.class));
    }

    @Test
    void testUpdateTask_InvalidStatus() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        CaseTaskUpdateRequest request = new CaseTaskUpdateRequest();
        request.setStatus("INVALID_STATUS");

        CaseTaskStatusInvalidException exception = assertThrows(CaseTaskStatusInvalidException.class, () -> {
            caseTaskService.updateTask(1L, request);
        });

        assertTrue(exception.getMessage().contains("无效的任务状态"));
        verify(caseTaskRepository, never()).save(any(CaseTask.class));
    }

    @Test
    void testBatchUpdateTaskStatus_Success() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(caseTaskRepository.findById(2L)).thenReturn(Optional.of(mockTask));
        when(caseTaskRepository.findById(3L)).thenReturn(Optional.of(mockTask));
        when(caseTaskRepository.save(any(CaseTask.class))).thenReturn(mockTask);

        List<Long> taskIds = Arrays.asList(1L, 2L, 3L);
        caseTaskService.batchUpdateTaskStatus(taskIds, CaseTaskStatus.COMPLETED.name());

        verify(caseTaskRepository, times(3)).save(any(CaseTask.class));
        assertEquals(CaseTaskStatus.COMPLETED.name(), mockTask.getStatus());
    }

    @Test
    void testDeleteTask_Success() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(caseTaskRepository.save(any(CaseTask.class))).thenReturn(mockTask);

        caseTaskService.deleteTask(1L);

        assertTrue(mockTask.getIsDeleted());
        verify(caseTaskRepository, times(1)).save(any(CaseTask.class));
    }

    @Test
    void testDeleteTask_NotFound() {
        when(caseTaskRepository.findById(999L)).thenReturn(Optional.empty());

        CaseTaskNotFoundException exception = assertThrows(CaseTaskNotFoundException.class, () -> {
            caseTaskService.deleteTask(999L);
        });

        assertTrue(exception.getMessage().contains("案件任务不存在"));
        verify(caseTaskRepository, never()).save(any(CaseTask.class));
    }

    @Test
    void testGetStatistics_Success() {
        List<CaseTask> tasks = Arrays.asList(
            createMockTask(1L, CaseTaskStatus.COMPLETED.name()),
            createMockTask(2L, CaseTaskStatus.IN_PROGRESS.name()),
            createMockTask(3L, CaseTaskStatus.REVIEWING.name()),
            createMockTask(4L, CaseTaskStatus.SKIPPED.name()),
            createMockTask(5L, CaseTaskStatus.REJECTED.name())
        );

        when(caseTaskRepository.findByCaseIdOrderBySortOrder(1L)).thenReturn(tasks);
        when(fileRecordRepository.findByConditions(eq("CASE_TASK"), any(String.class), eq("ACTIVE"), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(mockFile)));

        CaseTaskStatistics result = caseTaskService.getStatistics(1L);

        assertNotNull(result);
        assertEquals(5, result.getTotalTasks());
        assertEquals(1, result.getCompletedTasks());
        assertEquals(1, result.getInProgressTasks());
        assertEquals(1, result.getReviewingTasks());
        assertEquals(1, result.getSkippedTasks());
        assertEquals(1, result.getRejectedTasks());
        assertEquals(20.0, result.getCompletionRate());
        assertEquals(5, result.getTotalFiles());
    }

    @Test
    void testGetStatistics_EmptyTasks() {
        when(caseTaskRepository.findByCaseIdOrderBySortOrder(1L)).thenReturn(Arrays.asList());

        CaseTaskStatistics result = caseTaskService.getStatistics(1L);

        assertNotNull(result);
        assertEquals(0, result.getTotalTasks());
        assertEquals(0, result.getCompletionRate());
        assertEquals(0, result.getTotalFiles());
    }

    private CaseTask createMockTask(Long id, String status) {
        CaseTask task = new CaseTask();
        task.setId(id);
        task.setCaseId(1L);
        task.setTaskCode("TASK_" + String.format("%03d", id));
        task.setTaskName("测试任务");
        task.setStatus(status);
        return task;
    }
}
