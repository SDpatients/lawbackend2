package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CaseCreateRequest;
import com.lawbackend2.lawbackend2.dto.CaseReviewRequest;
import com.lawbackend2.lawbackend2.dto.CaseStatusUpdateRequest;
import com.lawbackend2.lawbackend2.dto.CaseUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.impl.BankruptCaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankruptCaseServiceTest {

    @Mock
    private BankruptCaseRepository bankruptCaseRepository;

    @InjectMocks
    private BankruptCaseServiceImpl bankruptCaseService;

    private CaseCreateRequest createRequest;
    private BankruptCase mockCase;

    @BeforeEach
    void setUp() {
        createRequest = new CaseCreateRequest();
        createRequest.setCaseNumber("TEST001");
        createRequest.setCaseName("测试案件");
        createRequest.setAcceptanceDate(LocalDate.now());
        createRequest.setCaseSource("法院受理");
        createRequest.setCaseReason("资不抵债");

        mockCase = new BankruptCase();
        mockCase.setId(1L);
        mockCase.setCaseNumber("TEST001");
        mockCase.setCaseName("测试案件");
        mockCase.setCaseStatus("PENDING");
        mockCase.setCaseProgress("FIRST");
        mockCase.setReviewStatus("PENDING");
    }

    @Test
    void testCreateCase_Success() {
        when(bankruptCaseRepository.findByCaseNumber("TEST001")).thenReturn(Optional.empty());
        when(bankruptCaseRepository.save(any(BankruptCase.class))).thenReturn(mockCase);

        BankruptCase result = bankruptCaseService.createCase(createRequest, 1L);

        assertNotNull(result);
        assertEquals("TEST001", result.getCaseNumber());
        assertEquals("测试案件", result.getCaseName());
        verify(bankruptCaseRepository, times(1)).save(any(BankruptCase.class));
    }

    @Test
    void testCreateCase_DuplicateCaseNumber() {
        when(bankruptCaseRepository.findByCaseNumber("TEST001")).thenReturn(Optional.of(mockCase));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            bankruptCaseService.createCase(createRequest, 1L);
        });

        assertEquals("案号已存在", exception.getMessage());
        verify(bankruptCaseRepository, never()).save(any(BankruptCase.class));
    }

    @Test
    void testGetCaseById_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));

        BankruptCase result = bankruptCaseService.getCaseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bankruptCaseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCaseById_NotFound() {
        when(bankruptCaseRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            bankruptCaseService.getCaseById(999L);
        });

        assertEquals("案件不存在", exception.getMessage());
    }

    @Test
    void testGetCaseList() {
        List<BankruptCase> cases = Arrays.asList(mockCase);
        Page<BankruptCase> page = new PageImpl<>(cases);
        when(bankruptCaseRepository.findByCaseStatus(eq("PENDING"), any(PageRequest.class)))
                .thenReturn(page);

        List<BankruptCase> result = bankruptCaseService.getCaseList(1, 10, "PENDING", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST001", result.get(0).getCaseNumber());
    }

    @Test
    void testUpdateCase_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(bankruptCaseRepository.save(any(BankruptCase.class))).thenReturn(mockCase);

        CaseUpdateRequest updateRequest = new CaseUpdateRequest();
        updateRequest.setCaseName("更新后的案件名称");

        BankruptCase result = bankruptCaseService.updateCase(1L, updateRequest);

        assertNotNull(result);
        verify(bankruptCaseRepository, times(1)).save(any(BankruptCase.class));
    }

    @Test
    void testUpdateCaseStatus_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(bankruptCaseRepository.save(any(BankruptCase.class))).thenReturn(mockCase);

        CaseStatusUpdateRequest statusRequest = new CaseStatusUpdateRequest();
        statusRequest.setCaseStatus("IN_PROGRESS");

        bankruptCaseService.updateCaseStatus(1L, statusRequest);

        verify(bankruptCaseRepository, times(1)).save(any(BankruptCase.class));
    }

    @Test
    void testReviewCase_Approved_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(bankruptCaseRepository.save(any(BankruptCase.class))).thenReturn(mockCase);

        CaseReviewRequest reviewRequest = new CaseReviewRequest();
        reviewRequest.setReviewStatus("APPROVED");
        reviewRequest.setReviewOpinion("审核通过");

        bankruptCaseService.reviewCase(1L, reviewRequest, 2L);

        verify(bankruptCaseRepository, times(1)).save(any(BankruptCase.class));
        assertEquals("APPROVED", mockCase.getReviewStatus());
        assertEquals("IN_PROGRESS", mockCase.getCaseStatus());
        assertEquals(2L, mockCase.getReviewerId());
        assertEquals("审核通过", mockCase.getReviewOpinion());
    }

    @Test
    void testReviewCase_Rejected_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));
        when(bankruptCaseRepository.save(any(BankruptCase.class))).thenReturn(mockCase);

        CaseReviewRequest reviewRequest = new CaseReviewRequest();
        reviewRequest.setReviewStatus("REJECTED");
        reviewRequest.setReviewOpinion("审核驳回");

        bankruptCaseService.reviewCase(1L, reviewRequest, 2L);

        verify(bankruptCaseRepository, times(1)).save(any(BankruptCase.class));
        assertEquals("REJECTED", mockCase.getReviewStatus());
        assertEquals("PENDING", mockCase.getCaseStatus());
        assertEquals(2L, mockCase.getReviewerId());
        assertEquals("审核驳回", mockCase.getReviewOpinion());
    }

    @Test
    void testReviewCase_NotPendingStatus() {
        mockCase.setReviewStatus("APPROVED");
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));

        CaseReviewRequest reviewRequest = new CaseReviewRequest();
        reviewRequest.setReviewStatus("APPROVED");
        reviewRequest.setReviewOpinion("审核通过");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            bankruptCaseService.reviewCase(1L, reviewRequest, 2L);
        });

        assertEquals("案件当前状态不允许审核", exception.getMessage());
        verify(bankruptCaseRepository, never()).save(any(BankruptCase.class));
    }

    @Test
    void testGetReviewStatus_Success() {
        when(bankruptCaseRepository.findById(1L)).thenReturn(Optional.of(mockCase));

        BankruptCase result = bankruptCaseService.getReviewStatus(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getReviewStatus());
        verify(bankruptCaseRepository, times(1)).findById(1L);
    }
}
