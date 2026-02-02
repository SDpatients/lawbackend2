package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.ApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ApprovalResponse;
import com.lawbackend2.lawbackend2.entity.Approval;
import com.lawbackend2.lawbackend2.entity.ApprovalHistory;
import com.lawbackend2.lawbackend2.entity.CaseTask;
import com.lawbackend2.lawbackend2.entity.CaseTaskSubmission;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.*;
import com.lawbackend2.lawbackend2.service.impl.ApprovalServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private ApprovalHistoryRepository approvalHistoryRepository;

    @Mock
    private BankruptCaseRepository bankruptCaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CaseTaskRepository caseTaskRepository;

    @Mock
    private CaseTaskSubmissionRepository caseTaskSubmissionRepository;

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApprovalServiceImpl approvalService;

    private Approval mockApproval;

    @BeforeEach
    void setUp() {
        mockApproval = new Approval();
        mockApproval.setId(1L);
        mockApproval.setCaseId(123L);
        mockApproval.setLawyerId(456L);
        mockApproval.setApprovalType("CASE_SUBMIT");
        mockApproval.setApprovalStatus("PENDING");
        mockApproval.setApprovalContent("案件提交审批");
        mockApproval.setApprovalCount(0);
        mockApproval.setStatus("ACTIVE");
        mockApproval.setCreateTime(LocalDateTime.now());
        mockApproval.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testCreateApproval_Success() {
        ApprovalCreateRequest request = new ApprovalCreateRequest();
        request.setCaseId(123L);
        request.setLawyerId(456L);
        request.setApprovalType("CASE_SUBMIT");
        request.setApprovalContent("案件提交审批");
        request.setRemark("请尽快审核");

        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);

        Long result = approvalService.createApproval(request, 123L);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(approvalRepository, times(1)).save(any(Approval.class));
    }

    @Test
    void testCreateApproval_CaseSubmit_WithFiles() throws Exception {
        ApprovalCreateRequest request = new ApprovalCreateRequest();
        request.setCaseId(16L);
        request.setApprovalType("CASE_SUBMIT");
        request.setApprovalTitle("案件审批 - 测试案件");
        request.setApprovalContent("案号：2026ceshi\n案件名称：测试案件\n受理法院：深圳市南山区人民法院\n案由：测试案件");
        request.setApprovalAttachment("");
        request.setRemark("");

        // Mock CaseSubmit files
        FileRecord mockFile1 = new FileRecord();
        mockFile1.setId(1L);
        mockFile1.setOriginalFileName("file1.pdf");
        mockFile1.setFileSize(1024L);
        mockFile1.setFileExtension("pdf");
        mockFile1.setBizId("16");

        FileRecord mockFile2 = new FileRecord();
        mockFile2.setId(2L);
        mockFile2.setOriginalFileName("file2.jpg");
        mockFile2.setFileSize(2048L);
        mockFile2.setFileExtension("jpg");
        mockFile2.setBizId("16");

        List<String> caseIds = Collections.singletonList("16");
        List<FileRecord> mockFiles = Arrays.asList(mockFile1, mockFile2);

        // Mock JSON serialization
        Map<String, Object> expectedAttachment = new HashMap<>();
        expectedAttachment.put("files", Arrays.asList(
            Collections.singletonMap("id", 1L),
            Collections.singletonMap("id", 2L)
        ));
        expectedAttachment.put("filesCount", 2);

        when(fileRecordRepository.findByBizTypeAndBizIds("CASE_SUBMIT", caseIds, null)).thenReturn(mockFiles);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"files\":[{},{}],\"filesCount\":2}");
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);

        Long result = approvalService.createApproval(request, 1L);

        assertNotNull(result);
        verify(fileRecordRepository, times(1)).findByBizTypeAndBizIds("CASE_SUBMIT", caseIds, null);
        verify(approvalRepository, times(1)).save(any(Approval.class));
    }

    @Test
    void testCreateApproval_TaskType_WithNonDeletedSubmissions() throws Exception {
        ApprovalCreateRequest request = new ApprovalCreateRequest();
        request.setCaseId(16L);
        request.setApprovalType("TASK_001");
        request.setApprovalTitle("任务审批");
        request.setApprovalContent("任务审批内容");
        request.setApprovalAttachment("");
        request.setRemark("");

        // Mock task
        CaseTask mockTask = new CaseTask();
        mockTask.setId(1L);
        mockTask.setTaskCode("TASK_001");
        mockTask.setTaskName("测试任务");

        // Mock submissions (only non-deleted ones)
        CaseTaskSubmission mockSubmission = new CaseTaskSubmission();
        mockSubmission.setId(1L);
        mockSubmission.setSubmissionTitle("测试提交");

        // Mock files
        FileRecord mockFile = new FileRecord();
        mockFile.setId(1L);
        mockFile.setOriginalFileName("test.pdf");
        mockFile.setBizId("1");

        when(caseTaskRepository.findByCaseIdAndTaskCode(16L, "TASK_001")).thenReturn(Optional.of(mockTask));
        when(caseTaskSubmissionRepository.findLatestByCaseTaskId(1L)).thenReturn(Collections.singletonList(mockSubmission));
        when(fileRecordRepository.findByBizTypeAndBizIds("CASE_TASK_SUBMISSION", Collections.singletonList("1"), null)).thenReturn(Collections.singletonList(mockFile));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"task\":{},\"submissions\":[{}]}");
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);

        Long result = approvalService.createApproval(request, 1L);

        assertNotNull(result);
        verify(caseTaskSubmissionRepository, times(1)).findLatestByCaseTaskId(1L);
        verify(approvalRepository, times(1)).save(any(Approval.class));
    }

    @Test
    void testGetApprovalList_Success() {
        when(approvalRepository.findAll()).thenReturn(Arrays.asList(mockApproval));

        PageResult<ApprovalResponse> result = approvalService.getApprovalList(1, 10, null, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        verify(approvalRepository, times(1)).findAll();
    }

    @Test
    void testGetApprovalDetail_Success() {
        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));

        ApprovalResponse result = approvalService.getApprovalDetail(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CASE_SUBMIT", result.getApprovalType());
        verify(approvalRepository, times(1)).findById(1L);
    }

    @Test
    void testGetApprovalDetail_NotFound() {
        when(approvalRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            approvalService.getApprovalDetail(999L);
        });

        assertTrue(exception.getMessage().contains("审批不存在"));
        verify(approvalRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateApproval_Success() {
        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);

        ApprovalUpdateRequest request = new ApprovalUpdateRequest();
        request.setApprovalContent("更新后的审批内容");
        request.setRemark("更新后的备注");

        approvalService.updateApproval(1L, request);

        assertEquals("更新后的审批内容", mockApproval.getApprovalContent());
        verify(approvalRepository, times(1)).save(any(Approval.class));
    }

    @Test
    void testApproveApproval_Success_Pass() {
        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(new ApprovalHistory());

        ApprovalRequest request = new ApprovalRequest();
        request.setApprovalResult("PASS");
        request.setApprovalOpinion("审核通过");
        request.setApproverId(789L);

        approvalService.approveApproval(1L, request, 789L);

        assertEquals("APPROVED", mockApproval.getApprovalStatus());
        assertEquals("PASS", mockApproval.getApprovalResult());
        assertEquals(789L, mockApproval.getApproverId());
        assertEquals(1, mockApproval.getApprovalCount());
        verify(approvalRepository, times(1)).save(any(Approval.class));
        verify(approvalHistoryRepository, times(1)).save(any(ApprovalHistory.class));
    }

    @Test
    void testApproveApproval_Success_Fail() {
        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(new ApprovalHistory());

        ApprovalRequest request = new ApprovalRequest();
        request.setApprovalResult("FAIL");
        request.setApprovalOpinion("审核不通过");
        request.setApproverId(789L);

        approvalService.approveApproval(1L, request, 789L);

        assertEquals("REJECTED", mockApproval.getApprovalStatus());
        assertEquals("FAIL", mockApproval.getApprovalResult());
        verify(approvalRepository, times(1)).save(any(Approval.class));
        verify(approvalHistoryRepository, times(1)).save(any(ApprovalHistory.class));
    }

    @Test
    void testApproveApproval_AlreadyApproved() {
        mockApproval.setApprovalStatus("APPROVED");
        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));

        ApprovalRequest request = new ApprovalRequest();
        request.setApprovalResult("PASS");
        request.setApprovalOpinion("审核通过");
        request.setApproverId(789L);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            approvalService.approveApproval(1L, request, 789L);
        });

        assertTrue(exception.getMessage().contains("该审批已处理，无法重复审批"));
        verify(approvalRepository, never()).save(any(Approval.class));
        verify(approvalHistoryRepository, never()).save(any(ApprovalHistory.class));
    }

    @Test
    void testUpdateApprovalStatus_Success() {
        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(mockApproval);

        ApprovalStatusRequest request = new ApprovalStatusRequest();
        request.setApprovalStatus("CANCELLED");

        approvalService.updateApprovalStatus(1L, request);

        assertEquals("CANCELLED", mockApproval.getApprovalStatus());
        verify(approvalRepository, times(1)).save(any(Approval.class));
    }

    @Test
    void testDeleteApproval_Success() {
        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        doNothing().when(approvalRepository).delete(any(Approval.class));

        approvalService.deleteApproval(1L);

        verify(approvalRepository, times(1)).delete(any(Approval.class));
    }

    @Test
    void testDeleteApproval_NotFound() {
        when(approvalRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            approvalService.deleteApproval(999L);
        });

        assertTrue(exception.getMessage().contains("审批不存在"));
        verify(approvalRepository, never()).delete(any(Approval.class));
    }

    @Test
    void testGetApprovalAttachments_ApprovalNotFound() {
        when(approvalRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            approvalService.getApprovalAttachments(999L, false, true);
        });

        assertTrue(exception.getMessage().contains("审批不存在"));
    }

    @Test
    void testGetApprovalAttachments_NonTaskApproval() {
        mockApproval.setApprovalType("CASE_SUBMIT"); // Not a task type

        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));

        Map<String, Object> result = approvalService.getApprovalAttachments(1L, false, true);

        assertNotNull(result);
        assertEquals(1L, result.get("approvalId"));
        Map<String, List<Map<String, Object>>> attachments = (Map<String, List<Map<String, Object>>>) result.get("attachments");
        assertNotNull(attachments);
        assertTrue(attachments.isEmpty());
        assertEquals(0, result.get("totalFiles"));
        assertEquals(0, result.get("imageFiles"));
    }

    @Test
    void testGetApprovalAttachments_TaskApproval_NoTask() {
        mockApproval.setApprovalType("TASK_TEST");

        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(caseTaskRepository.findByCaseIdAndTaskCode(mockApproval.getCaseId(), "TASK_TEST")).thenReturn(Optional.empty());

        Map<String, Object> result = approvalService.getApprovalAttachments(1L, false, true);

        assertNotNull(result);
        assertEquals(1L, result.get("approvalId"));
        Map<String, List<Map<String, Object>>> attachments = (Map<String, List<Map<String, Object>>>) result.get("attachments");
        assertNotNull(attachments);
        assertTrue(attachments.isEmpty());
        assertEquals(0, result.get("totalFiles"));
        assertEquals(0, result.get("imageFiles"));
    }

    @Test
    void testGetApprovalAttachments_TaskApproval_NoSubmissions() {
        mockApproval.setApprovalType("TASK_TEST");

        CaseTask mockTask = new CaseTask();
        mockTask.setId(10L);
        mockTask.setTaskCode("TASK_TEST");

        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(caseTaskRepository.findByCaseIdAndTaskCode(mockApproval.getCaseId(), "TASK_TEST")).thenReturn(Optional.of(mockTask));
        when(caseTaskSubmissionRepository.findByCaseTaskId(mockTask.getId())).thenReturn(Collections.emptyList());

        Map<String, Object> result = approvalService.getApprovalAttachments(1L, false, true);

        assertNotNull(result);
        assertEquals(1L, result.get("approvalId"));
        Map<String, List<Map<String, Object>>> attachments = (Map<String, List<Map<String, Object>>>) result.get("attachments");
        assertNotNull(attachments);
        assertTrue(attachments.isEmpty());
        assertEquals(0, result.get("totalFiles"));
        assertEquals(0, result.get("imageFiles"));
    }

    @Test
    void testGetApprovalAttachments_TaskApproval_NoFiles() {
        mockApproval.setApprovalType("TASK_TEST");

        CaseTask mockTask = new CaseTask();
        mockTask.setId(10L);
        mockTask.setTaskCode("TASK_TEST");

        CaseTaskSubmission mockSubmission = new CaseTaskSubmission();
        mockSubmission.setId(20L);

        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(caseTaskRepository.findByCaseIdAndTaskCode(mockApproval.getCaseId(), "TASK_TEST")).thenReturn(Optional.of(mockTask));
        when(caseTaskSubmissionRepository.findByCaseTaskId(mockTask.getId())).thenReturn(Collections.singletonList(mockSubmission));
        when(fileRecordRepository.findByBizTypeAndBizIds("CASE_TASK_SUBMISSION", Collections.singletonList("20"), null)).thenReturn(Collections.emptyList());

        Map<String, Object> result = approvalService.getApprovalAttachments(1L, false, true);

        assertNotNull(result);
        assertEquals(1L, result.get("approvalId"));
        Map<String, List<Map<String, Object>>> attachments = (Map<String, List<Map<String, Object>>>) result.get("attachments");
        assertNotNull(attachments);
        assertTrue(attachments.isEmpty());
        assertEquals(0, result.get("totalFiles"));
        assertEquals(0, result.get("imageFiles"));
    }

    @Test
    void testGetApprovalAttachments_TaskApproval_WithFiles() {
        mockApproval.setApprovalType("TASK_TEST");

        CaseTask mockTask = new CaseTask();
        mockTask.setId(10L);
        mockTask.setTaskCode("TASK_TEST");

        CaseTaskSubmission mockSubmission1 = new CaseTaskSubmission();
        mockSubmission1.setId(20L);

        CaseTaskSubmission mockSubmission2 = new CaseTaskSubmission();
        mockSubmission2.setId(21L);

        FileRecord mockFile1 = new FileRecord();
        mockFile1.setId(30L);
        mockFile1.setOriginalFileName("document1.pdf");
        mockFile1.setFileSize(1024L);
        mockFile1.setFileExtension("pdf");
        mockFile1.setMimeType("application/pdf");
        mockFile1.setBizId("20");

        FileRecord mockFile2 = new FileRecord();
        mockFile2.setId(31L);
        mockFile2.setOriginalFileName("image1.jpg");
        mockFile2.setFileSize(2048L);
        mockFile2.setFileExtension("jpg");
        mockFile2.setMimeType("image/jpeg");
        mockFile2.setBizId("20");

        FileRecord mockFile3 = new FileRecord();
        mockFile3.setId(32L);
        mockFile3.setOriginalFileName("document2.pdf");
        mockFile3.setFileSize(3072L);
        mockFile3.setFileExtension("pdf");
        mockFile3.setMimeType("application/pdf");
        mockFile3.setBizId("21");

        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(caseTaskRepository.findByCaseIdAndTaskCode(mockApproval.getCaseId(), "TASK_TEST")).thenReturn(Optional.of(mockTask));
        when(caseTaskSubmissionRepository.findByCaseTaskId(mockTask.getId())).thenReturn(Arrays.asList(mockSubmission1, mockSubmission2));
        when(fileRecordRepository.findByBizTypeAndBizIds("CASE_TASK_SUBMISSION", Arrays.asList("20", "21"), null)).thenReturn(Arrays.asList(mockFile1, mockFile2, mockFile3));

        Map<String, Object> result = approvalService.getApprovalAttachments(1L, false, true);

        assertNotNull(result);
        assertEquals(1L, result.get("approvalId"));
        Map<String, List<Map<String, Object>>> attachments = (Map<String, List<Map<String, Object>>>) result.get("attachments");
        assertNotNull(attachments);
        assertEquals(2, attachments.size());
        assertTrue(attachments.containsKey("20"));
        assertTrue(attachments.containsKey("21"));
        assertEquals(2, attachments.get("20").size());
        assertEquals(1, attachments.get("21").size());
        assertEquals(3, result.get("totalFiles"));
        assertEquals(1, result.get("imageFiles"));
    }

    @Test
    void testGetApprovalAttachments_IncludeFilesFalse() {
        mockApproval.setApprovalType("TASK_TEST");

        CaseTask mockTask = new CaseTask();
        mockTask.setId(10L);
        mockTask.setTaskCode("TASK_TEST");

        CaseTaskSubmission mockSubmission = new CaseTaskSubmission();
        mockSubmission.setId(20L);

        FileRecord mockFile = new FileRecord();
        mockFile.setId(30L);
        mockFile.setOriginalFileName("document1.pdf");
        mockFile.setFileSize(1024L);
        mockFile.setFileExtension("pdf");
        mockFile.setMimeType("application/pdf");
        mockFile.setBizId("20");

        when(approvalRepository.findById(1L)).thenReturn(Optional.of(mockApproval));
        when(caseTaskRepository.findByCaseIdAndTaskCode(mockApproval.getCaseId(), "TASK_TEST")).thenReturn(Optional.of(mockTask));
        when(caseTaskSubmissionRepository.findByCaseTaskId(mockTask.getId())).thenReturn(Collections.singletonList(mockSubmission));
        when(fileRecordRepository.findByBizTypeAndBizIds("CASE_TASK_SUBMISSION", Collections.singletonList("20"), null)).thenReturn(Collections.singletonList(mockFile));

        Map<String, Object> result = approvalService.getApprovalAttachments(1L, false, false);

        assertNotNull(result);
        assertEquals(1L, result.get("approvalId"));
        Map<String, List<Map<String, Object>>> attachments = (Map<String, List<Map<String, Object>>>) result.get("attachments");
        assertNotNull(attachments);
        assertTrue(attachments.isEmpty());
    }
}
