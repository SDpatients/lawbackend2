package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.FundBudgetApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundBudgetCreateRequest;
import com.lawbackend2.lawbackend2.entity.FundBudget;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundBudgetRepository;
import com.lawbackend2.lawbackend2.service.FundBudgetService;
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
class FundBudgetServiceImplTest {

    @Mock
    private FundBudgetRepository fundBudgetRepository;

    @InjectMocks
    private FundBudgetServiceImpl fundBudgetService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_BUDGET_ID = 1L;
    private static final String TEST_BUDGET_TYPE = "INITIAL";
    private static final String TEST_BUDGET_NAME = "初始预算";
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("100000.00");

    private FundBudgetCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new FundBudgetCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setBudgetType(TEST_BUDGET_TYPE);
        createRequest.setBudgetName(TEST_BUDGET_NAME);
        createRequest.setTotalBudgetAmount(TEST_AMOUNT);

        FundBudget mockBudget = new FundBudget();
        mockBudget.setId(TEST_BUDGET_ID);
        mockBudget.setCaseId(TEST_CASE_ID);
        mockBudget.setBudgetType(TEST_BUDGET_TYPE);
        mockBudget.setBudgetName(TEST_BUDGET_NAME);
        mockBudget.setTotalBudgetAmount(TEST_AMOUNT);
        mockBudget.setApprovalStatus("PENDING");
        mockBudget.setBudgetStatus("ACTIVE");

        when(fundBudgetRepository.save(any(FundBudget.class))).thenReturn(mockBudget);
        when(fundBudgetRepository.findById(TEST_BUDGET_ID))
                .thenReturn(Optional.of(mockBudget));
        when(fundBudgetRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateFundBudget_Success() {
        Long budgetId = fundBudgetService.createFundBudget(createRequest);

        assertNotNull(budgetId);
        assertEquals(TEST_BUDGET_ID, budgetId);
        verify(fundBudgetRepository, times(1)).save(any(FundBudget.class));
    }

    @Test
    void testGetFundBudgetDetail_Success() {
        FundBudget budget = fundBudgetService.getFundBudgetDetail(TEST_BUDGET_ID);

        assertNotNull(budget);
        assertEquals(TEST_BUDGET_ID, budget.getId());
        assertEquals(TEST_CASE_ID, budget.getCaseId());
        verify(fundBudgetRepository, times(1)).findById(TEST_BUDGET_ID);
    }

    @Test
    void testGetFundBudgetDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            fundBudgetService.getFundBudgetDetail(999L);
        });

        assertEquals("资金预算不存在", exception.getMessage());
    }

    @Test
    void testApproveFundBudget_Success() {
        FundBudgetApprovalRequest approvalRequest = new FundBudgetApprovalRequest();
        approvalRequest.setApprovalStatus("APPROVED");

        fundBudgetService.approveFundBudget(TEST_BUDGET_ID, approvalRequest);

        verify(fundBudgetRepository, times(1)).save(any(FundBudget.class));
    }

    @Test
    void testDeleteFundBudget_Success() {
        fundBudgetService.deleteFundBudget(TEST_BUDGET_ID);

        verify(fundBudgetRepository, times(1)).delete(any(FundBudget.class));
    }
}