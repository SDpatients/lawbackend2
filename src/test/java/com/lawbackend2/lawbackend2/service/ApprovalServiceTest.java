package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.ApprovalCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.ApprovalUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.ApprovalResponse;
import com.lawbackend2.lawbackend2.entity.Approval;
import com.lawbackend2.lawbackend2.entity.ApprovalHistory;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ApprovalHistoryRepository;
import com.lawbackend2.lawbackend2.repository.ApprovalRepository;
import com.lawbackend2.lawbackend2.service.impl.ApprovalServiceImpl;
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
import java.util.Arrays;
import java.util.Optional;

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

        approvalService.approveApproval(1L, request);

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

        approvalService.approveApproval(1L, request);

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
            approvalService.approveApproval(1L, request);
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
}