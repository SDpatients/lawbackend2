package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CaseTaskSubmissionReviewRequest;
import com.lawbackend2.lawbackend2.dto.response.CaseTaskSubmissionResponse;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import com.lawbackend2.lawbackend2.enums.SubmissionStatus;
import com.lawbackend2.lawbackend2.enums.SubmissionType;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.exception.CaseTaskNotFoundException;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskSubmissionRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.impl.CaseTaskSubmissionServiceImpl;
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
class CaseTaskSubmissionServiceTest {

    @Mock
    private CaseTaskSubmissionRepository submissionRepository;

    @Mock
    private CaseTaskRepository caseTaskRepository;

    @InjectMocks
    private CaseTaskSubmissionServiceImpl submissionService;

    private CaseTask mockTask;
    private CaseTaskSubmissionCreateRequest createRequest;
    private CaseTaskSubmissionReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        mockTask = new CaseTask();
        mockTask.setId(1L);
        mockTask.setCaseId(1L);
        mockTask.setTaskCode("TASK_001");
        mockTask.setTaskName("测试任务");

        createRequest = new CaseTaskSubmissionCreateRequest();
        createRequest.setCaseTaskId(1L);
        createRequest.setSubmissionTitle("提交标题");
        createRequest.setSubmissionContent("提交内容");

        reviewRequest = new CaseTaskSubmissionReviewRequest();
        reviewRequest.setReviewOpinion("审核意见");
        reviewRequest.setStatus(SubmissionStatus.APPROVED.name());
    }

    @Test
    void testCreateSubmission_Success() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(submissionRepository.getMaxSubmissionNumberByCaseTaskId(1L)).thenReturn(null);
        when(submissionRepository.save(any(CaseTaskSubmission.class))).thenAnswer(invocation -> {
            CaseTaskSubmission submission = invocation.getArgument(0);
            submission.setId(1L);
            return submission;
        });

        CaseTaskSubmissionResponse result = submissionService.createSubmission(createRequest, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getSubmissionNumber());
        assertEquals(SubmissionStatus.PENDING.name(), result.getStatus());
        verify(submissionRepository, times(1)).save(any(CaseTaskSubmission.class));
    }

    @Test
    void testCreateSubmission_TaskNotFound() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.empty());

        CaseTaskNotFoundException exception = assertThrows(CaseTaskNotFoundException.class, () -> {
            submissionService.createSubmission(createRequest, 1L);
        });

        assertTrue(exception.getMessage().contains("案件任务不存在"));
        verify(submissionRepository, never()).save(any(CaseTaskSubmission.class));
    }

    @Test
    void testCreateSubmission_IncrementSubmissionNumber() {
        when(caseTaskRepository.findById(1L)).thenReturn(Optional.of(mockTask));
        when(submissionRepository.getMaxSubmissionNumberByCaseTaskId(1L)).thenReturn(2);

        when(submissionRepository.save(any(CaseTaskSubmission.class))).thenAnswer(invocation -> {
            CaseTaskSubmission submission = invocation.getArgument(0);
            submission.setId(1L);
            return submission;
        });

        CaseTaskSubmissionResponse result = submissionService.createSubmission(createRequest, 1L);

        assertEquals(3, result.getSubmissionNumber());
    }

    @Test
    void testGetSubmissionsByTaskId_Success() {
        CaseTaskSubmission submission = new CaseTaskSubmission();
        submission.setId(1L);
        submission.setSubmissionTitle("测试提交");

        List<CaseTaskSubmission> submissions = Arrays.asList(submission);
        Page<CaseTaskSubmission> page = new PageImpl<>(submissions);
        when(submissionRepository.findByCaseTaskId(eq(1L), any(PageRequest.class))).thenReturn(page);

        Page<CaseTaskSubmissionResponse> result = submissionService.getSubmissionsByTaskId(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(submissionRepository, times(1)).findByCaseTaskId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetSubmissionById_Success() {
        CaseTaskSubmission submission = new CaseTaskSubmission();
        submission.setId(1L);
        submission.setSubmissionTitle("测试提交");

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        CaseTaskSubmissionResponse result = submissionService.getSubmissionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试提交", result.getSubmissionTitle());
        verify(submissionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSubmissionById_NotFound() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            submissionService.getSubmissionById(1L);
        });

        assertTrue(exception.getMessage().contains("提交记录不存在"));
    }

    @Test
    void testReviewSubmission_Success() {
        CaseTaskSubmission submission = new CaseTaskSubmission();
        submission.setId(1L);
        submission.setStatus(SubmissionStatus.PENDING.name());

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(CaseTaskSubmission.class))).thenReturn(submission);

        CaseTaskSubmissionResponse result = submissionService.reviewSubmission(1L, reviewRequest, 2L);

        assertNotNull(result);
        assertEquals(SubmissionStatus.APPROVED.name(), result.getStatus());
        assertEquals("审核意见", result.getReviewOpinion());
        assertEquals(2L, result.getReviewerId());
        verify(submissionRepository, times(1)).save(any(CaseTaskSubmission.class));
    }

    @Test
    void testReviewSubmission_NotPendingStatus() {
        CaseTaskSubmission submission = new CaseTaskSubmission();
        submission.setId(1L);
        submission.setStatus(SubmissionStatus.APPROVED.name());

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            submissionService.reviewSubmission(1L, reviewRequest, 2L);
        });

        assertTrue(exception.getMessage().contains("只能审核待审核状态的提交"));
        verify(submissionRepository, never()).save(any(CaseTaskSubmission.class));
    }

    @Test
    void testDeleteSubmission_Success() {
        CaseTaskSubmission submission = new CaseTaskSubmission();
        submission.setId(1L);

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(CaseTaskSubmission.class))).thenReturn(submission);

        submissionService.deleteSubmission(1L, 1L);

        assertTrue(submission.getIsDeleted());
        verify(submissionRepository, times(1)).findById(1L);
        verify(submissionRepository, times(1)).save(any(CaseTaskSubmission.class));
    }

    @Test
    void testDeleteSubmission_NotFound() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            submissionService.deleteSubmission(1L, 1L);
        });

        assertTrue(exception.getMessage().contains("提交记录不存在"));
        verify(submissionRepository, never()).save(any(CaseTaskSubmission.class));
    }

    @Test
    void testGetLatestSubmissions_Success() {
        CaseTaskSubmission submission1 = new CaseTaskSubmission();
        submission1.setId(1L);
        submission1.setSubmissionNumber(3);

        CaseTaskSubmission submission2 = new CaseTaskSubmission();
        submission2.setId(2L);
        submission2.setSubmissionNumber(2);

        when(submissionRepository.findLatestByCaseTaskId(1L)).thenReturn(Arrays.asList(submission1, submission2));

        List<CaseTaskSubmissionResponse> result = submissionService.getLatestSubmissions(1L, 5);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(submissionRepository, times(1)).findLatestByCaseTaskId(1L);
    }

    @Test
    void testGetLatestSubmissions_WithLimit() {
        CaseTaskSubmission submission1 = new CaseTaskSubmission();
        submission1.setId(1L);
        submission1.setSubmissionNumber(3);

        CaseTaskSubmission submission2 = new CaseTaskSubmission();
        submission2.setId(2L);
        submission2.setSubmissionNumber(2);

        CaseTaskSubmission submission3 = new CaseTaskSubmission();
        submission3.setId(3L);
        submission3.setSubmissionNumber(1);

        when(submissionRepository.findLatestByCaseTaskId(1L)).thenReturn(Arrays.asList(submission1, submission2, submission3));

        List<CaseTaskSubmissionResponse> result = submissionService.getLatestSubmissions(1L, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(3, result.get(0).getSubmissionNumber());
        assertEquals(2, result.get(1).getSubmissionNumber());
    }
}
