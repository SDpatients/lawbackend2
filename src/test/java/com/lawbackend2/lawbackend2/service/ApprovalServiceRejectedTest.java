package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.request.ApprovalCreateRequest;
import com.lawbackend2.lawbackend2.entity.Approval;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.ApprovalRepository;
import com.lawbackend2.lawbackend2.repository.ApprovalHistoryRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.impl.ApprovalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApprovalServiceRejectedTest {

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private ApprovalHistoryRepository approvalHistoryRepository;

    @Mock
    private BankruptCaseRepository bankruptCaseRepository;

    @InjectMocks
    private ApprovalServiceImpl approvalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateApproval_ExistingRejected_ShouldUpdate() {
        // 准备测试数据
        ApprovalCreateRequest request = new ApprovalCreateRequest();
        request.setCaseId(5L);
        request.setApprovalType("CASE_SUBMIT");
        request.setApprovalTitle("案件审批 - 上海某实业公司破产重整案");
        request.setApprovalContent("案号：（2024）沪02破1号\n案件名称：上海某实业公司破产重整案\n受理法院：上海市第二中级人民法院\n案由：经营困难");
        request.setRemark("请尽快审核");

        // 模拟已存在的被拒绝的审批
        Approval existingApproval = new Approval();
        existingApproval.setId(1L);
        existingApproval.setCaseId(5L);
        existingApproval.setApprovalType("CASE_SUBMIT");
        existingApproval.setApprovalStatus("REJECTED");
        existingApproval.setApprovalResult("FAIL");
        existingApproval.setApproverId(100L);
        existingApproval.setApprovalDate(LocalDateTime.now().minusDays(1));
        existingApproval.setCreateTime(LocalDateTime.now().minusDays(2));
        existingApproval.setCreateUserId(200L);
        existingApproval.setApprovalCount(1);

        when(approvalRepository.findByCaseIdAndApprovalType(5L, "CASE_SUBMIT")).thenReturn(Optional.of(existingApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(existingApproval);

        // 执行测试
        Long approvalId = approvalService.createApproval(request, 300L);

        // 验证结果
        assertNotNull(approvalId);
        assertEquals(1L, approvalId);
        verify(approvalRepository, times(1)).findByCaseIdAndApprovalType(5L, "CASE_SUBMIT");
        verify(approvalRepository, times(1)).save(existingApproval);
        
        // 验证审批状态已更新
        assertEquals("PENDING", existingApproval.getApprovalStatus());
        assertNull(existingApproval.getApprovalResult());
        assertNull(existingApproval.getApproverId());
        assertNull(existingApproval.getApprovalDate());
        assertEquals(300L, existingApproval.getUpdateUserId());
        assertNotNull(existingApproval.getUpdateTime());
        
        // 验证其他字段已更新
        assertEquals(request.getApprovalTitle(), existingApproval.getApprovalTitle());
        assertEquals(request.getApprovalContent(), existingApproval.getApprovalContent());
        assertEquals(request.getRemark(), existingApproval.getRemark());
    }

    @Test
    void testCreateApproval_ExistingPending_ShouldThrowException() {
        // 准备测试数据
        ApprovalCreateRequest request = new ApprovalCreateRequest();
        request.setCaseId(5L);
        request.setApprovalType("CASE_SUBMIT");

        // 模拟已存在的待审核的审批
        Approval existingApproval = new Approval();
        existingApproval.setId(1L);
        existingApproval.setCaseId(5L);
        existingApproval.setApprovalType("CASE_SUBMIT");
        existingApproval.setApprovalStatus("PENDING");

        when(approvalRepository.findByCaseIdAndApprovalType(5L, "CASE_SUBMIT")).thenReturn(Optional.of(existingApproval));

        // 执行测试，预期抛出异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            approvalService.createApproval(request, 300L);
        });

        // 验证结果
        assertTrue(exception.getMessage().contains("该案件已存在相同类型的审批申请"));
        verify(approvalRepository, times(1)).findByCaseIdAndApprovalType(5L, "CASE_SUBMIT");
        verify(approvalRepository, never()).save(any(Approval.class));
    }

    @Test
    void testCreateApproval_ExistingApproved_ShouldThrowException() {
        // 准备测试数据
        ApprovalCreateRequest request = new ApprovalCreateRequest();
        request.setCaseId(5L);
        request.setApprovalType("CASE_SUBMIT");

        // 模拟已存在的已通过的审批
        Approval existingApproval = new Approval();
        existingApproval.setId(1L);
        existingApproval.setCaseId(5L);
        existingApproval.setApprovalType("CASE_SUBMIT");
        existingApproval.setApprovalStatus("APPROVED");

        when(approvalRepository.findByCaseIdAndApprovalType(5L, "CASE_SUBMIT")).thenReturn(Optional.of(existingApproval));

        // 执行测试，预期抛出异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            approvalService.createApproval(request, 300L);
        });

        // 验证结果
        assertTrue(exception.getMessage().contains("该案件已存在相同类型的审批申请"));
        verify(approvalRepository, times(1)).findByCaseIdAndApprovalType(5L, "CASE_SUBMIT");
        verify(approvalRepository, never()).save(any(Approval.class));
    }

    @Test
    void testCreateApproval_ExistingFailResult_ShouldUpdate() {
        // 准备测试数据
        ApprovalCreateRequest request = new ApprovalCreateRequest();
        request.setCaseId(5L);
        request.setApprovalType("CASE_SUBMIT");
        request.setApprovalTitle("更新后的案件审批");
        request.setApprovalContent("更新后的审批内容");
        request.setRemark("请重新审核");

        // 模拟已存在的审批结果为FAIL的审批
        Approval existingApproval = new Approval();
        existingApproval.setId(1L);
        existingApproval.setCaseId(5L);
        existingApproval.setApprovalType("CASE_SUBMIT");
        existingApproval.setApprovalStatus("COMPLETED"); // 状态为已完成，但结果为失败
        existingApproval.setApprovalResult("FAIL");
        existingApproval.setApproverId(100L);
        existingApproval.setApprovalDate(LocalDateTime.now().minusDays(1));
        existingApproval.setCreateTime(LocalDateTime.now().minusDays(2));
        existingApproval.setCreateUserId(200L);
        existingApproval.setApprovalCount(1);

        when(approvalRepository.findByCaseIdAndApprovalType(5L, "CASE_SUBMIT")).thenReturn(Optional.of(existingApproval));
        when(approvalRepository.save(any(Approval.class))).thenReturn(existingApproval);

        // 执行测试
        Long approvalId = approvalService.createApproval(request, 300L);

        // 验证结果
        assertNotNull(approvalId);
        assertEquals(1L, approvalId);
        verify(approvalRepository, times(1)).findByCaseIdAndApprovalType(5L, "CASE_SUBMIT");
        verify(approvalRepository, times(1)).save(existingApproval);
        
        // 验证审批状态已更新
        assertEquals("PENDING", existingApproval.getApprovalStatus());
        assertNull(existingApproval.getApprovalResult());
        assertNull(existingApproval.getApproverId());
        assertNull(existingApproval.getApprovalDate());
        assertEquals(300L, existingApproval.getUpdateUserId());
        assertNotNull(existingApproval.getUpdateTime());
        
        // 验证其他字段已更新
        assertEquals(request.getApprovalTitle(), existingApproval.getApprovalTitle());
        assertEquals(request.getApprovalContent(), existingApproval.getApprovalContent());
        assertEquals(request.getRemark(), existingApproval.getRemark());
    }
}