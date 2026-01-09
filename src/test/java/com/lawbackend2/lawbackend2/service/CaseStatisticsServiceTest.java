package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CaseStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CaseStatisticsResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.impl.CaseStatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseStatisticsServiceTest {

    @Mock
    private BankruptCaseRepository caseRepository;

    @InjectMocks
    private CaseStatisticsServiceImpl caseStatisticsService;

    private CaseStatisticsRequest request;

    @BeforeEach
    void setUp() {
        request = new CaseStatisticsRequest();
    }

    @Test
    void testGetCaseStatistics_Success() {
        when(caseRepository.countTotalCases()).thenReturn(100L);
        when(caseRepository.countByCaseStatus("PENDING")).thenReturn(10L);
        when(caseRepository.countByCaseStatus("IN_PROGRESS")).thenReturn(20L);
        when(caseRepository.countByCaseStatus("APPROVED")).thenReturn(30L);
        when(caseRepository.countByCaseStatus("COMPLETED")).thenReturn(25L);
        when(caseRepository.countByCaseStatus("CLOSED")).thenReturn(10L);
        when(caseRepository.countByCaseStatus("TERMINATED")).thenReturn(3L);
        when(caseRepository.countByCaseStatus("ARCHIVED")).thenReturn(2L);
        when(caseRepository.countByIsSimplifiedTrial(true)).thenReturn(40L);
        when(caseRepository.countByIsSimplifiedTrial(false)).thenReturn(60L);
        when(caseRepository.getAverageReviewCount()).thenReturn(2.5);
        when(caseRepository.countByCreatedAtDate(LocalDate.now())).thenReturn(5L);
        when(caseRepository.countByCreatedAtYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue())).thenReturn(15L);
        when(caseRepository.countByCreatedAtYear(LocalDate.now().getYear())).thenReturn(50L);

        List<Object[]> statusGroup = Arrays.asList(
                new Object[]{"PENDING", 10L},
                new Object[]{"IN_PROGRESS", 20L}
        );
        when(caseRepository.countByCaseStatusGroup()).thenReturn(statusGroup);

        List<Object[]> progressGroup = Arrays.asList(
                new Object[]{"FIRST", 30L},
                new Object[]{"SECOND", 40L}
        );
        when(caseRepository.countByCaseProgressGroup()).thenReturn(progressGroup);

        CaseStatisticsResponse response = caseStatisticsService.getCaseStatistics(request);

        assertNotNull(response);
        assertEquals(100L, response.getTotalCases());
        assertEquals(10L, response.getPendingCases());
        assertEquals(20L, response.getInProgressCases());
        assertEquals(30L, response.getApprovedCases());
        assertEquals(25L, response.getCompletedCases());
        assertEquals(10L, response.getClosedCases());
        assertEquals(3L, response.getTerminatedCases());
        assertEquals(2L, response.getArchivedCases());
        assertEquals(40L, response.getSimplifiedTrialCases());
        assertEquals(60L, response.getNormalTrialCases());
        assertEquals(new BigDecimal("2.50"), response.getAverageReviewCount());
        assertEquals(5L, response.getTodayCreatedCases());
        assertEquals(15L, response.getMonthCreatedCases());
        assertEquals(50L, response.getYearCreatedCases());
        assertNotNull(response.getStatusDistribution());
        assertNotNull(response.getProgressDistribution());

        verify(caseRepository, times(1)).countTotalCases();
        verify(caseRepository, times(7)).countByCaseStatus(anyString());
        verify(caseRepository, times(2)).countByIsSimplifiedTrial(anyBoolean());
    }

    @Test
    void testGetCaseStatistics_WithDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        when(caseRepository.countByCreatedAtBetween(startDate, endDate)).thenReturn(50L);
        when(caseRepository.countByCaseStatus("PENDING")).thenReturn(5L);
        when(caseRepository.countByCaseStatus("IN_PROGRESS")).thenReturn(10L);
        when(caseRepository.countByCaseStatus("APPROVED")).thenReturn(15L);
        when(caseRepository.countByCaseStatus("COMPLETED")).thenReturn(12L);
        when(caseRepository.countByCaseStatus("CLOSED")).thenReturn(5L);
        when(caseRepository.countByCaseStatus("TERMINATED")).thenReturn(2L);
        when(caseRepository.countByCaseStatus("ARCHIVED")).thenReturn(1L);
        when(caseRepository.countByIsSimplifiedTrial(true)).thenReturn(20L);
        when(caseRepository.countByIsSimplifiedTrial(false)).thenReturn(30L);
        when(caseRepository.getAverageReviewCount()).thenReturn(2.0);
        when(caseRepository.countByCreatedAtDate(LocalDate.now())).thenReturn(3L);
        when(caseRepository.countByCreatedAtYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue())).thenReturn(8L);
        when(caseRepository.countByCreatedAtYear(LocalDate.now().getYear())).thenReturn(25L);

        List<Object[]> statusGroup = Arrays.asList(
                new Object[]{"PENDING", 5L},
                new Object[]{"IN_PROGRESS", 10L}
        );
        when(caseRepository.countByCaseStatusGroupByDateRange(startDate, endDate)).thenReturn(statusGroup);

        List<Object[]> progressGroup = Arrays.asList(
                new Object[]{"FIRST", 15L},
                new Object[]{"SECOND", 20L}
        );
        when(caseRepository.countByCaseProgressGroupByDateRange(startDate, endDate)).thenReturn(progressGroup);

        CaseStatisticsResponse response = caseStatisticsService.getCaseStatistics(request);

        assertNotNull(response);
        assertEquals(50L, response.getTotalCases());

        verify(caseRepository, times(1)).countByCreatedAtBetween(any(), any());
        verify(caseRepository, times(1)).countByCaseStatusGroupByDateRange(any(), any());
        verify(caseRepository, times(1)).countByCaseProgressGroupByDateRange(any(), any());
    }

    @Test
    void testGetCaseStatistics_InvalidDateRange() {
        LocalDate startDate = LocalDate.of(2024, 12, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            caseStatisticsService.getCaseStatistics(request);
        });

        assertEquals("开始日期不能晚于结束日期", exception.getMessage());
        verify(caseRepository, never()).countTotalCases();
    }

    @Test
    void testGetCaseStatistics_NullCounts() {
        when(caseRepository.countTotalCases()).thenReturn(0L);
        when(caseRepository.countByCaseStatus(anyString())).thenReturn(null);
        when(caseRepository.countByIsSimplifiedTrial(anyBoolean())).thenReturn(null);
        when(caseRepository.getAverageReviewCount()).thenReturn(null);
        when(caseRepository.countByCreatedAtDate(any())).thenReturn(null);
        when(caseRepository.countByCreatedAtYearAndMonth(anyInt(), anyInt())).thenReturn(null);
        when(caseRepository.countByCreatedAtYear(anyInt())).thenReturn(null);

        List<Object[]> statusGroup = Arrays.asList();
        when(caseRepository.countByCaseStatusGroup()).thenReturn(statusGroup);

        List<Object[]> progressGroup = Arrays.asList();
        when(caseRepository.countByCaseProgressGroup()).thenReturn(progressGroup);

        CaseStatisticsResponse response = caseStatisticsService.getCaseStatistics(request);

        assertNotNull(response);
        assertEquals(0L, response.getTotalCases());
        assertEquals(0L, response.getPendingCases());
        assertEquals(0L, response.getInProgressCases());
        assertEquals(BigDecimal.ZERO, response.getAverageReviewCount());
        assertEquals(0L, response.getTodayCreatedCases());
        assertEquals(0L, response.getMonthCreatedCases());
        assertEquals(0L, response.getYearCreatedCases());
    }
}
