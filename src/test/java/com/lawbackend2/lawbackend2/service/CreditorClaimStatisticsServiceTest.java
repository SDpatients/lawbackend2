package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsResponse;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CreditorClaimRepository;
import com.lawbackend2.lawbackend2.service.impl.CreditorClaimStatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditorClaimStatisticsServiceTest {

    @Mock
    private CreditorClaimRepository creditorClaimRepository;

    @InjectMocks
    private CreditorClaimStatisticsServiceImpl creditorClaimStatisticsService;

    private CreditorClaimStatisticsRequest request;

    @BeforeEach
    void setUp() {
        request = new CreditorClaimStatisticsRequest();
    }

    @Test
    void testGetCreditorClaimStatistics_Success() {
        when(creditorClaimRepository.countTotalClaims()).thenReturn(100L);
        when(creditorClaimRepository.countByRegistrationStatus("PENDING")).thenReturn(30L);
        when(creditorClaimRepository.countByRegistrationStatus("REGISTERED")).thenReturn(60L);
        when(creditorClaimRepository.countByRegistrationStatus("REJECTED")).thenReturn(10L);
        when(creditorClaimRepository.sumPrincipal()).thenReturn(Optional.of(new BigDecimal("1000000.00")));
        when(creditorClaimRepository.sumInterest()).thenReturn(Optional.of(new BigDecimal("100000.00")));
        when(creditorClaimRepository.sumPenalty()).thenReturn(Optional.of(new BigDecimal("50000.00")));
        when(creditorClaimRepository.sumOtherLosses()).thenReturn(Optional.of(new BigDecimal("20000.00")));
        when(creditorClaimRepository.sumTotalAmount()).thenReturn(Optional.of(new BigDecimal("1170000.00")));
        when(creditorClaimRepository.getAverageClaimAmount()).thenReturn(Optional.of(11700.0));
        when(creditorClaimRepository.countByCreatedAtDate(LocalDate.now())).thenReturn(5L);
        when(creditorClaimRepository.countByCreatedAtYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue())).thenReturn(15L);
        when(creditorClaimRepository.countByCreatedAtYear(LocalDate.now().getYear())).thenReturn(50L);
        when(creditorClaimRepository.countByHasCourtJudgment(true)).thenReturn(40L);
        when(creditorClaimRepository.countByHasExecution(true)).thenReturn(30L);
        when(creditorClaimRepository.countByHasCollateral(true)).thenReturn(20L);

        List<Object[]> statusGroup = Arrays.asList(
                new Object[]{"PENDING", 30L},
                new Object[]{"REGISTERED", 60L},
                new Object[]{"REJECTED", 10L}
        );
        when(creditorClaimRepository.countByRegistrationStatusGroup()).thenReturn(statusGroup);

        List<Object[]> claimTypeGroup = Arrays.asList(
                new Object[]{"PRINCIPAL", 50L},
                new Object[]{"INTEREST", 30L}
        );
        when(creditorClaimRepository.countByClaimTypeGroup()).thenReturn(claimTypeGroup);

        List<Object[]> claimNatureGroup = Arrays.asList(
                new Object[]{"SECURED", 40L},
                new Object[]{"UNSECURED", 60L}
        );
        when(creditorClaimRepository.countByClaimNatureGroup()).thenReturn(claimNatureGroup);

        CreditorClaimStatisticsResponse response = creditorClaimStatisticsService.getCreditorClaimStatistics(request);

        assertNotNull(response);
        assertEquals(100L, response.getTotalClaims());
        assertEquals(30L, response.getPendingClaims());
        assertEquals(60L, response.getRegisteredClaims());
        assertEquals(10L, response.getRejectedClaims());
        assertEquals(new BigDecimal("1000000.00"), response.getTotalPrincipalAmount());
        assertEquals(new BigDecimal("100000.00"), response.getTotalInterestAmount());
        assertEquals(new BigDecimal("50000.00"), response.getTotalPenaltyAmount());
        assertEquals(new BigDecimal("20000.00"), response.getTotalOtherLossesAmount());
        assertEquals(new BigDecimal("1170000.00"), response.getTotalClaimAmount());
        assertEquals(new BigDecimal("11700.00"), response.getAverageClaimAmount());
        assertEquals(5L, response.getTodayCreatedClaims());
        assertEquals(15L, response.getMonthCreatedClaims());
        assertEquals(50L, response.getYearCreatedClaims());
        assertEquals(40L, response.getHasCourtJudgmentClaims());
        assertEquals(30L, response.getHasExecutionClaims());
        assertEquals(20L, response.getHasCollateralClaims());
        assertNotNull(response.getStatusDistribution());
        assertNotNull(response.getClaimTypeDistribution());
        assertNotNull(response.getClaimNatureDistribution());

        verify(creditorClaimRepository, times(1)).countTotalClaims();
        verify(creditorClaimRepository, times(3)).countByRegistrationStatus(anyString());
        verify(creditorClaimRepository, times(1)).sumPrincipal();
        verify(creditorClaimRepository, times(1)).sumInterest();
        verify(creditorClaimRepository, times(1)).sumPenalty();
        verify(creditorClaimRepository, times(1)).sumOtherLosses();
        verify(creditorClaimRepository, times(1)).sumTotalAmount();
        verify(creditorClaimRepository, times(1)).getAverageClaimAmount();
    }

    @Test
    void testGetCreditorClaimStatistics_WithDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        when(creditorClaimRepository.countByCreatedAtBetween(startDate, endDate)).thenReturn(50L);
        when(creditorClaimRepository.countByRegistrationStatus("PENDING")).thenReturn(15L);
        when(creditorClaimRepository.countByRegistrationStatus("REGISTERED")).thenReturn(30L);
        when(creditorClaimRepository.countByRegistrationStatus("REJECTED")).thenReturn(5L);
        when(creditorClaimRepository.sumPrincipal()).thenReturn(Optional.of(new BigDecimal("500000.00")));
        when(creditorClaimRepository.sumInterest()).thenReturn(Optional.of(new BigDecimal("50000.00")));
        when(creditorClaimRepository.sumPenalty()).thenReturn(Optional.of(new BigDecimal("25000.00")));
        when(creditorClaimRepository.sumOtherLosses()).thenReturn(Optional.of(new BigDecimal("10000.00")));
        when(creditorClaimRepository.sumTotalAmount()).thenReturn(Optional.of(new BigDecimal("585000.00")));
        when(creditorClaimRepository.getAverageClaimAmount()).thenReturn(Optional.of(11700.0));
        when(creditorClaimRepository.countByCreatedAtDate(LocalDate.now())).thenReturn(3L);
        when(creditorClaimRepository.countByCreatedAtYearAndMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue())).thenReturn(8L);
        when(creditorClaimRepository.countByCreatedAtYear(LocalDate.now().getYear())).thenReturn(25L);
        when(creditorClaimRepository.countByHasCourtJudgment(true)).thenReturn(20L);
        when(creditorClaimRepository.countByHasExecution(true)).thenReturn(15L);
        when(creditorClaimRepository.countByHasCollateral(true)).thenReturn(10L);

        List<Object[]> statusGroup = Arrays.asList(
                new Object[]{"PENDING", 15L},
                new Object[]{"REGISTERED", 30L},
                new Object[]{"REJECTED", 5L}
        );
        when(creditorClaimRepository.countByRegistrationStatusGroupByDateRange(startDate, endDate)).thenReturn(statusGroup);

        List<Object[]> claimTypeGroup = Arrays.asList(
                new Object[]{"PRINCIPAL", 25L},
                new Object[]{"INTEREST", 15L}
        );
        when(creditorClaimRepository.countByClaimTypeGroupByDateRange(startDate, endDate)).thenReturn(claimTypeGroup);

        List<Object[]> claimNatureGroup = Arrays.asList(
                new Object[]{"SECURED", 20L},
                new Object[]{"UNSECURED", 30L}
        );
        when(creditorClaimRepository.countByClaimNatureGroupByDateRange(startDate, endDate)).thenReturn(claimNatureGroup);

        CreditorClaimStatisticsResponse response = creditorClaimStatisticsService.getCreditorClaimStatistics(request);

        assertNotNull(response);
        assertEquals(50L, response.getTotalClaims());

        verify(creditorClaimRepository, times(1)).countByCreatedAtBetween(any(), any());
        verify(creditorClaimRepository, times(1)).countByRegistrationStatusGroupByDateRange(any(), any());
        verify(creditorClaimRepository, times(1)).countByClaimTypeGroupByDateRange(any(), any());
        verify(creditorClaimRepository, times(1)).countByClaimNatureGroupByDateRange(any(), any());
    }

    @Test
    void testGetCreditorClaimStatistics_InvalidDateRange() {
        LocalDate startDate = LocalDate.of(2024, 12, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            creditorClaimStatisticsService.getCreditorClaimStatistics(request);
        });

        assertEquals("开始日期不能晚于结束日期", exception.getMessage());
        verify(creditorClaimRepository, never()).countTotalClaims();
    }

    @Test
    void testGetCreditorClaimStatistics_NullCounts() {
        when(creditorClaimRepository.countTotalClaims()).thenReturn(0L);
        when(creditorClaimRepository.countByRegistrationStatus(anyString())).thenReturn(null);
        when(creditorClaimRepository.sumPrincipal()).thenReturn(Optional.empty());
        when(creditorClaimRepository.sumInterest()).thenReturn(Optional.empty());
        when(creditorClaimRepository.sumPenalty()).thenReturn(Optional.empty());
        when(creditorClaimRepository.sumOtherLosses()).thenReturn(Optional.empty());
        when(creditorClaimRepository.sumTotalAmount()).thenReturn(Optional.empty());
        when(creditorClaimRepository.getAverageClaimAmount()).thenReturn(Optional.empty());
        when(creditorClaimRepository.countByCreatedAtDate(any())).thenReturn(null);
        when(creditorClaimRepository.countByCreatedAtYearAndMonth(anyInt(), anyInt())).thenReturn(null);
        when(creditorClaimRepository.countByCreatedAtYear(anyInt())).thenReturn(null);
        when(creditorClaimRepository.countByHasCourtJudgment(true)).thenReturn(null);
        when(creditorClaimRepository.countByHasExecution(true)).thenReturn(null);
        when(creditorClaimRepository.countByHasCollateral(true)).thenReturn(null);

        List<Object[]> statusGroup = Arrays.asList();
        when(creditorClaimRepository.countByRegistrationStatusGroup()).thenReturn(statusGroup);

        List<Object[]> claimTypeGroup = Arrays.asList();
        when(creditorClaimRepository.countByClaimTypeGroup()).thenReturn(claimTypeGroup);

        List<Object[]> claimNatureGroup = Arrays.asList();
        when(creditorClaimRepository.countByClaimNatureGroup()).thenReturn(claimNatureGroup);

        CreditorClaimStatisticsResponse response = creditorClaimStatisticsService.getCreditorClaimStatistics(request);

        assertNotNull(response);
        assertEquals(0L, response.getTotalClaims());
        assertEquals(0L, response.getPendingClaims());
        assertEquals(0L, response.getRegisteredClaims());
        assertEquals(BigDecimal.ZERO, response.getTotalPrincipalAmount());
        assertEquals(BigDecimal.ZERO, response.getTotalInterestAmount());
        assertEquals(BigDecimal.ZERO, response.getTotalPenaltyAmount());
        assertEquals(BigDecimal.ZERO, response.getTotalOtherLossesAmount());
        assertEquals(BigDecimal.ZERO, response.getTotalClaimAmount());
        assertEquals(BigDecimal.ZERO, response.getAverageClaimAmount());
        assertEquals(0L, response.getTodayCreatedClaims());
        assertEquals(0L, response.getMonthCreatedClaims());
        assertEquals(0L, response.getYearCreatedClaims());
        assertEquals(0L, response.getHasCourtJudgmentClaims());
        assertEquals(0L, response.getHasExecutionClaims());
        assertEquals(0L, response.getHasCollateralClaims());
    }
}
