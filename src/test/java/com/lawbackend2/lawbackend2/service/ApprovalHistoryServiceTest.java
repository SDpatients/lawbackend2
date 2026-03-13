package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.response.ApprovalHistoryResponse;
import com.lawbackend2.lawbackend2.entity.ApprovalHistory;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ApprovalHistoryRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskRepository;
import com.lawbackend2.lawbackend2.repository.CaseTaskSubmissionRepository;
import com.lawbackend2.lawbackend2.repository.FileRecordRepository;
import com.lawbackend2.lawbackend2.service.impl.ApprovalHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalHistoryServiceTest {

    @Mock
    private ApprovalHistoryRepository approvalHistoryRepository;
    @Mock
    private CaseTaskRepository caseTaskRepository;
    @Mock
    private CaseTaskSubmissionRepository caseTaskSubmissionRepository;
    @Mock
    private FileRecordRepository fileRecordRepository;

    @InjectMocks
    private ApprovalHistoryServiceImpl approvalHistoryService;

    private ApprovalHistory mockHistory;

    @BeforeEach
    void setUp() {
        mockHistory = new ApprovalHistory();
        mockHistory.setId(1L);
        mockHistory.setApprovalId(1L);
        mockHistory.setCaseId(123L);
        mockHistory.setApproverId(789L);
        mockHistory.setApprovalType("CASE_SUBMIT");
        mockHistory.setApprovalStatus("APPROVED");
        mockHistory.setApprovalOpinion("审核通过");
        mockHistory.setApprovalDate(LocalDateTime.now());
        mockHistory.setStatus("ACTIVE");
        mockHistory.setCreateTime(LocalDateTime.now());
        mockHistory.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testGetApprovalHistoryList_Success() {
        Page<ApprovalHistory> page = new PageImpl<>(Arrays.asList(mockHistory));
        when(approvalHistoryRepository.findAll(any(PageRequest.class))).thenReturn(page);

        PageResult<ApprovalHistoryResponse> result = approvalHistoryService.getApprovalHistoryList(1, 10, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        verify(approvalHistoryRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetApprovalHistoryDetail_Success() {
        when(approvalHistoryRepository.findById(1L)).thenReturn(Optional.of(mockHistory));

        ApprovalHistoryResponse result = approvalHistoryService.getApprovalHistoryDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getApprovalId());
        assertEquals("APPROVED", result.getApprovalStatus());
        verify(approvalHistoryRepository, times(1)).findById(1L);
    }

    @Test
    void testGetApprovalHistoryDetail_NotFound() {
        when(approvalHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            approvalHistoryService.getApprovalHistoryDetail(999L);
        });

        assertTrue(exception.getMessage().contains("审批历史不存在"));
        verify(approvalHistoryRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateApprovalHistory_WithTaskType() {
        // Setup mock objects
        mockHistory.setApprovalType("TASK_001");
        mockHistory.setApprovalAttachment(null);
        
        // Create mock CaseTask
        CaseTask mockCaseTask = new CaseTask();
        mockCaseTask.setId(100L);
        mockCaseTask.setCaseId(123L);
        mockCaseTask.setTaskCode("001");
        
        // Create mock CaseTaskSubmission
        CaseTaskSubmission mockSubmission = new CaseTaskSubmission();
        mockSubmission.setId(200L);
        mockSubmission.setCaseTaskId(100L);
        
        // Create mock FileRecords
        FileRecord mockFile1 = new FileRecord();
        mockFile1.setId(300L);
        mockFile1.setBizType("CASE_TASK_SUBMISSION");
        mockFile1.setBizId("200");
        
        FileRecord mockFile2 = new FileRecord();
        mockFile2.setId(301L);
        mockFile2.setBizType("CASE_TASK_SUBMISSION");
        mockFile2.setBizId("200");
        
        // Mock repository methods
        when(caseTaskRepository.findByCaseIdAndTaskCode(123L, "001")).thenReturn(Optional.of(mockCaseTask));
        when(caseTaskSubmissionRepository.findFirstByCaseTaskIdOrderBySubmissionNumberDesc(100L)).thenReturn(Optional.of(mockSubmission));
        when(fileRecordRepository.findByConditions("CASE_TASK_SUBMISSION", "200", null, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList(mockFile1, mockFile2)));
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(mockHistory);
        
        // Call the method
        ApprovalHistory result = approvalHistoryService.createApprovalHistory(mockHistory);
        
        // Verify the result
        assertNotNull(result);
        assertEquals("300,301", result.getApprovalAttachment());
        
        // Verify repository calls
        verify(caseTaskRepository, times(1)).findByCaseIdAndTaskCode(123L, "001");
        verify(caseTaskSubmissionRepository, times(1)).findFirstByCaseTaskIdOrderBySubmissionNumberDesc(100L);
        verify(fileRecordRepository, times(1)).findByConditions("CASE_TASK_SUBMISSION", "200", null, Pageable.unpaged());
        verify(approvalHistoryRepository, times(1)).save(any(ApprovalHistory.class));
    }

    @Test
    void testCreateApprovalHistory_WithTaskType_NoFiles() {
        // Setup mock objects
        mockHistory.setApprovalType("TASK_001");
        mockHistory.setApprovalAttachment(null);
        
        // Create mock CaseTask
        CaseTask mockCaseTask = new CaseTask();
        mockCaseTask.setId(100L);
        mockCaseTask.setCaseId(123L);
        mockCaseTask.setTaskCode("001");
        
        // Create mock CaseTaskSubmission
        CaseTaskSubmission mockSubmission = new CaseTaskSubmission();
        mockSubmission.setId(200L);
        mockSubmission.setCaseTaskId(100L);
        
        // Mock repository methods
        when(caseTaskRepository.findByCaseIdAndTaskCode(123L, "001")).thenReturn(Optional.of(mockCaseTask));
        when(caseTaskSubmissionRepository.findFirstByCaseTaskIdOrderBySubmissionNumberDesc(100L)).thenReturn(Optional.of(mockSubmission));
        when(fileRecordRepository.findByConditions("CASE_TASK_SUBMISSION", "200", null, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(Arrays.asList()));
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(mockHistory);
        
        // Call the method
        ApprovalHistory result = approvalHistoryService.createApprovalHistory(mockHistory);
        
        // Verify the result
        assertNotNull(result);
        assertNull(result.getApprovalAttachment());
        
        // Verify repository calls
        verify(caseTaskRepository, times(1)).findByCaseIdAndTaskCode(123L, "001");
        verify(caseTaskSubmissionRepository, times(1)).findFirstByCaseTaskIdOrderBySubmissionNumberDesc(100L);
        verify(fileRecordRepository, times(1)).findByConditions("CASE_TASK_SUBMISSION", "200", null, Pageable.unpaged());
        verify(approvalHistoryRepository, times(1)).save(any(ApprovalHistory.class));
    }

    @Test
    void testCreateApprovalHistory_WithNormalType() {
        // Setup mock objects
        mockHistory.setApprovalType("CASE_SUBMIT");
        mockHistory.setApprovalAttachment(null);
        
        // Mock repository method
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(mockHistory);
        
        // Call the method
        ApprovalHistory result = approvalHistoryService.createApprovalHistory(mockHistory);
        
        // Verify the result
        assertNotNull(result);
        assertNull(result.getApprovalAttachment());
        
        // Verify repository calls - should not call other repositories
        verify(caseTaskRepository, never()).findByCaseIdAndTaskCode(anyLong(), anyString());
        verify(caseTaskSubmissionRepository, never()).findFirstByCaseTaskIdOrderBySubmissionNumberDesc(anyLong());
        verify(fileRecordRepository, never()).findByConditions(anyString(), anyString(), anyString(), any(Pageable.class));
        verify(approvalHistoryRepository, times(1)).save(any(ApprovalHistory.class));
    }
}