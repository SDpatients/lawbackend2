package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.EscrowManagementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.EscrowManagementReleaseRequest;
import com.lawbackend2.lawbackend2.entity.EscrowManagement;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.EscrowManagementRepository;
import com.lawbackend2.lawbackend2.service.EscrowManagementService;
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
class EscrowManagementServiceImplTest {

    @Mock
    private EscrowManagementRepository escrowManagementRepository;

    @InjectMocks
    private EscrowManagementServiceImpl escrowManagementService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_ESCROW_ID = 1L;
    private static final String TEST_ESCROW_TYPE = "CONDITIONAL";
    private static final String TEST_ESCROW_NAME = "附条件债权提存";
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("20000.00");

    private EscrowManagementCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new EscrowManagementCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setEscrowType(TEST_ESCROW_TYPE);
        createRequest.setEscrowName(TEST_ESCROW_NAME);
        createRequest.setEscrowAmount(TEST_AMOUNT);
        createRequest.setCreditorName("测试债权人");

        EscrowManagement mockEscrow = new EscrowManagement();
        mockEscrow.setId(TEST_ESCROW_ID);
        mockEscrow.setCaseId(TEST_CASE_ID);
        mockEscrow.setEscrowType(TEST_ESCROW_TYPE);
        mockEscrow.setEscrowName(TEST_ESCROW_NAME);
        mockEscrow.setEscrowAmount(TEST_AMOUNT);
        mockEscrow.setReleaseStatus("UNRELEASED");

        when(escrowManagementRepository.save(any(EscrowManagement.class))).thenReturn(mockEscrow);
        when(escrowManagementRepository.findById(TEST_ESCROW_ID))
                .thenReturn(Optional.of(mockEscrow));
        when(escrowManagementRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateEscrowManagement_Success() {
        Long escrowId = escrowManagementService.createEscrowManagement(createRequest);

        assertNotNull(escrowId);
        assertEquals(TEST_ESCROW_ID, escrowId);
        verify(escrowManagementRepository, times(1)).save(any(EscrowManagement.class));
    }

    @Test
    void testGetEscrowManagementDetail_Success() {
        EscrowManagement escrow = escrowManagementService.getEscrowManagementDetail(TEST_ESCROW_ID);

        assertNotNull(escrow);
        assertEquals(TEST_ESCROW_ID, escrow.getId());
        assertEquals(TEST_CASE_ID, escrow.getCaseId());
        verify(escrowManagementRepository, times(1)).findById(TEST_ESCROW_ID);
    }

    @Test
    void testGetEscrowManagementDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            escrowManagementService.getEscrowManagementDetail(999L);
        });

        assertEquals("提存记录不存在", exception.getMessage());
    }

    @Test
    void testReleaseEscrow_Success() {
        EscrowManagementReleaseRequest releaseRequest = new EscrowManagementReleaseRequest();
        releaseRequest.setReleaseStatus("RELEASED");
        releaseRequest.setReleasedAmount(TEST_AMOUNT);

        escrowManagementService.releaseEscrow(TEST_ESCROW_ID, releaseRequest);

        verify(escrowManagementRepository, times(1)).save(any(EscrowManagement.class));
    }

    @Test
    void testDeleteEscrowManagement_Success() {
        escrowManagementService.deleteEscrowManagement(TEST_ESCROW_ID);

        verify(escrowManagementRepository, times(1)).delete(any(EscrowManagement.class));
    }
}