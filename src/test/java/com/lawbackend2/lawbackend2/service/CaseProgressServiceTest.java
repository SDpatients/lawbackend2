package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CaseProgressCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseProgressUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseProgress;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.CaseProgressRepository;
import com.lawbackend2.lawbackend2.service.impl.CaseProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseProgressServiceTest {

    @Mock
    private CaseProgressRepository caseProgressRepository;

    @Mock
    private BankruptCaseRepository caseRepository;

    @InjectMocks
    private CaseProgressServiceImpl caseProgressService;

    private CaseProgressCreateRequest createRequest;
    private CaseProgressUpdateRequest updateRequest;
    private BankruptCase mockCase;
    private CaseProgress mockProgress;

    @BeforeEach
    void setUp() {
        createRequest = new CaseProgressCreateRequest();
        createRequest.setCaseId(1L);
        createRequest.setCaseName("测试案件");
        createRequest.setCaseNumber("CASE001");
        createRequest.setProgressStage("FIRST");
        createRequest.setStageName("第一阶段");
        createRequest.setStageDescription("第一阶段描述");
        createRequest.setStartDate(LocalDate.now());
        createRequest.setExpectedEndDate(LocalDate.now().plusDays(30));
        createRequest.setProgressStatus("IN_PROGRESS");
        createRequest.setCompletionPercentage(50);
        createRequest.setKeyTasks("任务1,任务2");
        createRequest.setCompletedTasks("任务1");
        createRequest.setPendingTasks("任务2");
        createRequest.setResponsiblePerson("张三");
        createRequest.setResponsiblePersonId(1L);
        createRequest.setRemarks("备注");

        updateRequest = new CaseProgressUpdateRequest();
        updateRequest.setStageName("更新后的阶段名称");
        updateRequest.setCompletionPercentage(80);
        updateRequest.setCompletedTasks("任务1,任务2");
        updateRequest.setPendingTasks("");

        mockCase = new BankruptCase();
        mockCase.setId(1L);
        mockCase.setCaseName("测试案件");
        mockCase.setCaseNumber("CASE001");

        mockProgress = new CaseProgress();
        mockProgress.setId(1L);
        mockProgress.setCaseId(1L);
        mockProgress.setCaseName("测试案件");
        mockProgress.setCaseNumber("CASE001");
        mockProgress.setProgressStage("FIRST");
        mockProgress.setStageName("第一阶段");
        mockProgress.setStageDescription("第一阶段描述");
        mockProgress.setStartDate(LocalDate.now());
        mockProgress.setExpectedEndDate(LocalDate.now().plusDays(30));
        mockProgress.setProgressStatus("IN_PROGRESS");
        mockProgress.setCompletionPercentage(50);
        mockProgress.setKeyTasks("任务1,任务2");
        mockProgress.setCompletedTasks("任务1");
        mockProgress.setPendingTasks("任务2");
        mockProgress.setResponsiblePerson("张三");
        mockProgress.setResponsiblePersonId(1L);
        mockProgress.setRemarks("备注");
        mockProgress.setIsCompleted(false);
    }

    @Test
    void testCreateProgress_Success() {
        when(caseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(caseProgressRepository.findByCaseIdAndProgressStage(1L, "FIRST")).thenReturn(Optional.empty());
        when(caseProgressRepository.save(any(CaseProgress.class))).thenReturn(mockProgress);

        CaseProgress result = caseProgressService.createProgress(createRequest, 1L);

        assertNotNull(result);
        assertEquals("FIRST", result.getProgressStage());
        assertEquals("第一阶段", result.getStageName());
        assertEquals("IN_PROGRESS", result.getProgressStatus());
        assertEquals(50, result.getCompletionPercentage());
        assertFalse(result.getIsCompleted());
        verify(caseProgressRepository, times(1)).save(any(CaseProgress.class));
    }

    @Test
    void testCreateProgress_CaseNotFound() {
        when(caseRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setCaseId(999L);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseProgressService.createProgress(createRequest, 1L);
        });

        assertEquals("案件不存在", exception.getMessage());
        verify(caseProgressRepository, never()).save(any(CaseProgress.class));
    }

    @Test
    void testCreateProgress_DuplicateProgressStage() {
        when(caseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(caseProgressRepository.findByCaseIdAndProgressStage(1L, "FIRST")).thenReturn(Optional.of(mockProgress));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseProgressService.createProgress(createRequest, 1L);
        });

        assertEquals("该案件已存在相同阶段的进度记录", exception.getMessage());
        verify(caseProgressRepository, never()).save(any(CaseProgress.class));
    }

    @Test
    void testGetProgressById_Success() {
        when(caseProgressRepository.findById(1L)).thenReturn(Optional.of(mockProgress));

        CaseProgress result = caseProgressService.getProgressById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("FIRST", result.getProgressStage());
        verify(caseProgressRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProgressById_NotFound() {
        when(caseProgressRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseProgressService.getProgressById(999L);
        });

        assertEquals("案件进度不存在", exception.getMessage());
    }

    @Test
    void testGetProgressList_WithCaseId() {
        when(caseProgressRepository.findByCaseIdOrderByStartDateDesc(eq(1L), any())).thenReturn(
                org.springframework.data.domain.Page.empty());

        List<CaseProgress> result = caseProgressService.getProgressList(1, 10, 1L, null, null, null);

        assertNotNull(result);
        verify(caseProgressRepository, times(1)).findByCaseIdOrderByStartDateDesc(eq(1L), any());
    }

    @Test
    void testGetProgressList_WithProgressStage() {
        when(caseProgressRepository.findByProgressStage(eq("FIRST"), any())).thenReturn(
                org.springframework.data.domain.Page.empty());

        List<CaseProgress> result = caseProgressService.getProgressList(1, 10, null, "FIRST", null, null);

        assertNotNull(result);
        verify(caseProgressRepository, times(1)).findByProgressStage(eq("FIRST"), any());
    }

    @Test
    void testGetProgressList_WithIsCompleted() {
        when(caseProgressRepository.findByCaseIdAndIsCompleted(eq(1L), eq(false), any())).thenReturn(
                org.springframework.data.domain.Page.empty());

        List<CaseProgress> result = caseProgressService.getProgressList(1, 10, 1L, null, null, false);

        assertNotNull(result);
        verify(caseProgressRepository, times(1)).findByCaseIdAndIsCompleted(eq(1L), eq(false), any());
    }

    @Test
    void testUpdateProgress_Success() {
        when(caseProgressRepository.findById(1L)).thenReturn(Optional.of(mockProgress));
        when(caseProgressRepository.save(any(CaseProgress.class))).thenReturn(mockProgress);

        CaseProgress result = caseProgressService.updateProgress(1L, updateRequest, 1L);

        assertNotNull(result);
        verify(caseProgressRepository, times(1)).save(any(CaseProgress.class));
    }

    @Test
    void testUpdateProgress_NotFound() {
        when(caseProgressRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseProgressService.updateProgress(999L, updateRequest, 1L);
        });

        assertEquals("案件进度不存在", exception.getMessage());
        verify(caseProgressRepository, never()).save(any(CaseProgress.class));
    }

    @Test
    void testCompleteProgress_Success() {
        when(caseProgressRepository.findById(1L)).thenReturn(Optional.of(mockProgress));
        when(caseProgressRepository.save(any(CaseProgress.class))).thenReturn(mockProgress);

        caseProgressService.completeProgress(1L, 1L, "张三");

        assertTrue(mockProgress.getIsCompleted());
        assertEquals(100, mockProgress.getCompletionPercentage());
        assertEquals("COMPLETED", mockProgress.getProgressStatus());
        assertNotNull(mockProgress.getCompletedAt());
        assertEquals(1L, mockProgress.getCompletedBy());
        verify(caseProgressRepository, times(1)).save(any(CaseProgress.class));
    }

    @Test
    void testCompleteProgress_AlreadyCompleted() {
        mockProgress.setIsCompleted(true);
        when(caseProgressRepository.findById(1L)).thenReturn(Optional.of(mockProgress));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseProgressService.completeProgress(1L, 1L, "张三");
        });

        assertEquals("该进度已完成", exception.getMessage());
        verify(caseProgressRepository, never()).save(any(CaseProgress.class));
    }

    @Test
    void testDeleteProgress_Success() {
        when(caseProgressRepository.existsById(1L)).thenReturn(true);

        caseProgressService.deleteProgress(1L);

        verify(caseProgressRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProgress_NotFound() {
        when(caseProgressRepository.existsById(999L)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseProgressService.deleteProgress(999L);
        });

        assertEquals("案件进度不存在", exception.getMessage());
        verify(caseProgressRepository, never()).deleteById(999L);
    }

    @Test
    void testGetProgressByCaseId_Success() {
        List<CaseProgress> progressList = Arrays.asList(mockProgress);
        when(caseProgressRepository.findByCaseIdOrderByStartDate(1L)).thenReturn(progressList);

        List<CaseProgress> result = caseProgressService.getProgressByCaseId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(caseProgressRepository, times(1)).findByCaseIdOrderByStartDate(1L);
    }

    @Test
    void testGetInProgressProgressByCaseId_Success() {
        List<CaseProgress> progressList = Arrays.asList(mockProgress);
        when(caseProgressRepository.findInProgressByCaseId(1L)).thenReturn(progressList);

        List<CaseProgress> result = caseProgressService.getInProgressProgressByCaseId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(caseProgressRepository, times(1)).findInProgressByCaseId(1L);
    }

    @Test
    void testGetCompletedProgressByCaseId_Success() {
        mockProgress.setIsCompleted(true);
        List<CaseProgress> progressList = Arrays.asList(mockProgress);
        when(caseProgressRepository.findCompletedByCaseId(1L)).thenReturn(progressList);

        List<CaseProgress> result = caseProgressService.getCompletedProgressByCaseId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(caseProgressRepository, times(1)).findCompletedByCaseId(1L);
    }

    @Test
    void testGetOverallProgressPercentage_Success() {
        when(caseProgressRepository.getAverageCompletionPercentage(1L)).thenReturn(75.0);

        Double percentage = caseProgressService.getOverallProgressPercentage(1L);

        assertEquals(75.0, percentage);
        verify(caseProgressRepository, times(1)).getAverageCompletionPercentage(1L);
    }

    @Test
    void testGetOverallProgressPercentage_Null() {
        when(caseProgressRepository.getAverageCompletionPercentage(1L)).thenReturn(null);

        Double percentage = caseProgressService.getOverallProgressPercentage(1L);

        assertEquals(0.0, percentage);
    }
}
