package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.DistributionExecutionCreateRequest;
import com.lawbackend2.lawbackend2.entity.DistributionExecution;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.DistributionExecutionRepository;
import com.lawbackend2.lawbackend2.service.DistributionExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DistributionExecutionServiceImplTest {

    @Mock
    private DistributionExecutionRepository distributionExecutionRepository;

    @InjectMocks
    private DistributionExecutionServiceImpl distributionExecutionService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_EXECUTION_ID = 1L;
    private static final String TEST_BATCH = "FIRST";
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("100000.00");

    private DistributionExecutionCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new DistributionExecutionCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setDistributionBatch(TEST_BATCH);
        createRequest.setTotalDistributableAmount(TEST_AMOUNT);
        createRequest.setTotalDistributionAmount(TEST_AMOUNT);

        DistributionExecution mockExecution = new DistributionExecution();
        mockExecution.setId(TEST_EXECUTION_ID);
        mockExecution.setCaseId(TEST_CASE_ID);
        mockExecution.setDistributionBatch(TEST_BATCH);
        mockExecution.setTotalDistributableAmount(TEST_AMOUNT);
        mockExecution.setTotalDistributionAmount(TEST_AMOUNT);
        mockExecution.setApprovalStatus("PENDING");
        mockExecution.setExecutionStatus("PENDING");

        when(distributionExecutionRepository.save(any(DistributionExecution.class))).thenReturn(mockExecution);
        when(distributionExecutionRepository.findById(TEST_EXECUTION_ID))
                .thenReturn(Optional.of(mockExecution));
        when(distributionExecutionRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateDistributionExecution_Success() {
        Long executionId = distributionExecutionService.createDistributionExecution(createRequest);

        assertNotNull(executionId);
        assertEquals(TEST_EXECUTION_ID, executionId);
        verify(distributionExecutionRepository, times(1)).save(any(DistributionExecution.class));
    }

    @Test
    void testGetDistributionExecutionDetail_Success() {
        DistributionExecution execution = distributionExecutionService.getDistributionExecutionDetail(TEST_EXECUTION_ID);

        assertNotNull(execution);
        assertEquals(TEST_EXECUTION_ID, execution.getId());
        assertEquals(TEST_CASE_ID, execution.getCaseId());
        verify(distributionExecutionRepository, times(1)).findById(TEST_EXECUTION_ID);
    }

    @Test
    void testGetDistributionExecutionDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            distributionExecutionService.getDistributionExecutionDetail(999L);
        });

        assertEquals("分配执行不存在", exception.getMessage());
    }

    @Test
    void testApproveDistributionExecution_Success() {
        DistributionExecutionApprovalRequest approvalRequest = new DistributionExecutionApprovalRequest();
        approvalRequest.setApprovalStatus("APPROVED");

        distributionExecutionService.approveDistributionExecution(TEST_EXECUTION_ID, approvalRequest);

        verify(distributionExecutionRepository, times(1)).save(any(DistributionExecution.class));
    }

    @Test
    void testExecuteDistribution_Success() {
        distributionExecutionService.executeDistribution(TEST_EXECUTION_ID);

        verify(distributionExecutionRepository, times(1)).save(any(DistributionExecution.class));
    }

    @Test
    void testDeleteDistributionExecution_Success() {
        distributionExecutionService.deleteDistributionExecution(TEST_EXECUTION_ID);

        verify(distributionExecutionRepository, times(1)).delete(any(DistributionExecution.class));
    }
}