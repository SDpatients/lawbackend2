package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.CommonDebtApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.CommonDebtRepaymentRequest;
import com.lawbackend2.lawbackend2.entity.CommonDebt;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CommonDebtRepository;
import com.lawbackend2.lawbackend2.service.CommonDebtService;
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
class CommonDebtServiceImplTest {

    @Mock
    private CommonDebtRepository commonDebtRepository;

    @InjectMocks
    private CommonDebtServiceImpl commonDebtService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_DEBT_ID = 1L;
    private static final String TEST_DEBT_TYPE = "CONTINUE_OPERATION";
    private static final String TEST_DEBT_NAME = "继续营业债务";
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("50000.00");

    private CommonDebtCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CommonDebtCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setDebtType(TEST_DEBT_TYPE);
        createRequest.setDebtName(TEST_DEBT_NAME);
        createRequest.setDebtAmount(TEST_AMOUNT);
        createRequest.setCreditorName("测试债权人");

        CommonDebt mockDebt = new CommonDebt();
        mockDebt.setId(TEST_DEBT_ID);
        mockDebt.setCaseId(TEST_CASE_ID);
        mockDebt.setDebtType(TEST_DEBT_TYPE);
        mockDebt.setDebtName(TEST_DEBT_NAME);
        mockDebt.setDebtAmount(TEST_AMOUNT);
        mockDebt.setApprovalStatus("PENDING");
        mockDebt.setRepaymentStatus("UNREPAID");

        when(commonDebtRepository.save(any(CommonDebt.class))).thenReturn(mockDebt);
        when(commonDebtRepository.findById(TEST_DEBT_ID))
                .thenReturn(Optional.of(mockDebt));
        when(commonDebtRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateCommonDebt_Success() {
        Long debtId = commonDebtService.createCommonDebt(createRequest);

        assertNotNull(debtId);
        assertEquals(TEST_DEBT_ID, debtId);
        verify(commonDebtRepository, times(1)).save(any(CommonDebt.class));
    }

    @Test
    void testGetCommonDebtDetail_Success() {
        CommonDebt debt = commonDebtService.getCommonDebtDetail(TEST_DEBT_ID);

        assertNotNull(debt);
        assertEquals(TEST_DEBT_ID, debt.getId());
        assertEquals(TEST_CASE_ID, debt.getCaseId());
        verify(commonDebtRepository, times(1)).findById(TEST_DEBT_ID);
    }

    @Test
    void testGetCommonDebtDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            commonDebtService.getCommonDebtDetail(999L);
        });

        assertEquals("共益债务不存在", exception.getMessage());
    }

    @Test
    void testApproveCommonDebt_Success() {
        CommonDebtApprovalRequest approvalRequest = new CommonDebtApprovalRequest();
        approvalRequest.setApprovalStatus("APPROVED");

        commonDebtService.approveCommonDebt(TEST_DEBT_ID, approvalRequest);

        verify(commonDebtRepository, times(1)).save(any(CommonDebt.class));
    }

    @Test
    void testRepayCommonDebt_Success() {
        CommonDebtRepaymentRequest repaymentRequest = new CommonDebtRepaymentRequest();
        repaymentRequest.setRepaymentStatus("REPAID");
        repaymentRequest.setRepaidAmount(TEST_AMOUNT);

        commonDebtService.repayCommonDebt(TEST_DEBT_ID, repaymentRequest);

        verify(commonDebtRepository, times(1)).save(any(CommonDebt.class));
    }

    @Test
    void testDeleteCommonDebt_Success() {
        commonDebtService.deleteCommonDebt(TEST_DEBT_ID);

        verify(commonDebtRepository, times(1)).delete(any(CommonDebt.class));
    }
}