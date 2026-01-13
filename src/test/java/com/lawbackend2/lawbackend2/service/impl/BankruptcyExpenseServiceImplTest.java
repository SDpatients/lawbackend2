package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpenseCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.BankruptcyExpensePaymentRequest;
import com.lawbackend2.lawbackend2.entity.BankruptcyExpense;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptcyExpenseRepository;
import com.lawbackend2.lawbackend2.service.BankruptcyExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BankruptcyExpenseServiceImplTest {

    @Mock
    private BankruptcyExpenseRepository bankruptcyExpenseRepository;

    @InjectMocks
    private BankruptcyExpenseServiceImpl bankruptcyExpenseService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_EXPENSE_ID = 1L;
    private static final String TEST_EXPENSE_TYPE = "LITIGATION";
    private static final String TEST_EXPENSE_NAME = "诉讼费用";
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("10000.00");

    private BankruptcyExpenseCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new BankruptcyExpenseCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setExpenseType(TEST_EXPENSE_TYPE);
        createRequest.setExpenseName(TEST_EXPENSE_NAME);
        createRequest.setAppliedAmount(TEST_AMOUNT);

        BankruptcyExpense mockExpense = new BankruptcyExpense();
        mockExpense.setId(TEST_EXPENSE_ID);
        mockExpense.setCaseId(TEST_CASE_ID);
        mockExpense.setExpenseType(TEST_EXPENSE_TYPE);
        mockExpense.setExpenseName(TEST_EXPENSE_NAME);
        mockExpense.setAppliedAmount(TEST_AMOUNT);
        mockExpense.setApprovalStatus("PENDING");
        mockExpense.setPaymentStatus("UNPAID");

        when(bankruptcyExpenseRepository.save(any(BankruptcyExpense.class))).thenReturn(mockExpense);
        when(bankruptcyExpenseRepository.findById(TEST_EXPENSE_ID))
                .thenReturn(Optional.of(mockExpense));
        when(bankruptcyExpenseRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateBankruptcyExpense_Success() {
        Long expenseId = bankruptcyExpenseService.createBankruptcyExpense(createRequest);

        assertNotNull(expenseId);
        assertEquals(TEST_EXPENSE_ID, expenseId);
        verify(bankruptcyExpenseRepository, times(1)).save(any(BankruptcyExpense.class));
    }

    @Test
    void testGetBankruptcyExpenseDetail_Success() {
        BankruptcyExpense expense = bankruptcyExpenseService.getBankruptcyExpenseDetail(TEST_EXPENSE_ID);

        assertNotNull(expense);
        assertEquals(TEST_EXPENSE_ID, expense.getId());
        assertEquals(TEST_CASE_ID, expense.getCaseId());
        verify(bankruptcyExpenseRepository, times(1)).findById(TEST_EXPENSE_ID);
    }

    @Test
    void testGetBankruptcyExpenseDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            bankruptcyExpenseService.getBankruptcyExpenseDetail(999L);
        });

        assertEquals("破产费用不存在", exception.getMessage());
    }

    @Test
    void testApproveBankruptcyExpense_Success() {
        BankruptcyExpenseApprovalRequest approvalRequest = new BankruptcyExpenseApprovalRequest();
        approvalRequest.setApprovalStatus("APPROVED");
        approvalRequest.setApprovedAmount(TEST_AMOUNT);

        bankruptcyExpenseService.approveBankruptcyExpense(TEST_EXPENSE_ID, approvalRequest);

        verify(bankruptcyExpenseRepository, times(1)).save(any(BankruptcyExpense.class));
    }

    @Test
    void testPayBankruptcyExpense_Success() {
        BankruptcyExpensePaymentRequest paymentRequest = new BankruptcyExpensePaymentRequest();
        paymentRequest.setPaymentStatus("PAID");
        paymentRequest.setPaidAmount(TEST_AMOUNT);

        bankruptcyExpenseService.payBankruptcyExpense(TEST_EXPENSE_ID, paymentRequest);

        verify(bankruptcyExpenseRepository, times(1)).save(any(BankruptcyExpense.class));
    }

    @Test
    void testDeleteBankruptcyExpense_Success() {
        bankruptcyExpenseService.deleteBankruptcyExpense(TEST_EXPENSE_ID);

        verify(bankruptcyExpenseRepository, times(1)).delete(any(BankruptcyExpense.class));
    }
}