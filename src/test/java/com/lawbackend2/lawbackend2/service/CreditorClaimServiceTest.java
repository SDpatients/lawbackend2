package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CreditorClaimCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimReviewRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimUpdateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorClaim;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CreditorClaimRepository;
import com.lawbackend2.lawbackend2.service.impl.CreditorClaimServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditorClaimServiceTest {

    @Mock
    private CreditorClaimRepository creditorClaimRepository;

    @InjectMocks
    private CreditorClaimServiceImpl creditorClaimService;

    private CreditorClaimCreateRequest createRequest;
    private CreditorClaim mockClaim;

    @BeforeEach
    void setUp() {
        createRequest = new CreditorClaimCreateRequest();
        createRequest.setCaseId(1L);
        createRequest.setCaseName("测试案件");
        createRequest.setCreditorName("测试债权人");
        createRequest.setCreditorType("企业");
        createRequest.setClaimType("普通债权");
        createRequest.setTotalAmount(new BigDecimal("1000000.00"));

        mockClaim = new CreditorClaim();
        mockClaim.setId(1L);
        mockClaim.setCaseId(1L);
        mockClaim.setCreditorName("测试债权人");
        mockClaim.setCreditorType("企业");
        mockClaim.setClaimType("普通债权");
        mockClaim.setTotalAmount(new BigDecimal("1000000.00"));
        mockClaim.setRegistrationStatus("PENDING");
    }

    @Test
    void testCreateClaim_Success() {
        when(creditorClaimRepository.save(any(CreditorClaim.class))).thenReturn(mockClaim);

        CreditorClaim result = creditorClaimService.createClaim(createRequest, 1L);

        assertNotNull(result);
        assertEquals("测试债权人", result.getCreditorName());
        assertEquals("普通债权", result.getClaimType());
        verify(creditorClaimRepository, times(1)).save(any(CreditorClaim.class));
    }

    @Test
    void testGetClaimById_Success() {
        when(creditorClaimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));

        CreditorClaim result = creditorClaimService.getClaimById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试债权人", result.getCreditorName());
        verify(creditorClaimRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClaimById_NotFound() {
        when(creditorClaimRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            creditorClaimService.getClaimById(999L);
        });

        assertEquals("债权申报不存在", exception.getMessage());
    }

    @Test
    void testGetClaimList() {
        List<CreditorClaim> claims = Arrays.asList(mockClaim);
        Page<CreditorClaim> page = new PageImpl<>(claims);
        when(creditorClaimRepository.findByCaseId(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        List<CreditorClaim> result = creditorClaimService.getClaimList(1, 10, 1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试债权人", result.get(0).getCreditorName());
    }

    @Test
    void testUpdateClaim_Success() {
        when(creditorClaimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));
        when(creditorClaimRepository.save(any(CreditorClaim.class))).thenReturn(mockClaim);

        CreditorClaimUpdateRequest updateRequest = new CreditorClaimUpdateRequest();
        updateRequest.setCreditorName("更新后的债权人");
        updateRequest.setTotalAmount(new BigDecimal("2000000.00"));

        CreditorClaim result = creditorClaimService.updateClaim(1L, updateRequest);

        assertNotNull(result);
        verify(creditorClaimRepository, times(1)).save(any(CreditorClaim.class));
    }

    @Test
    void testReviewClaim_Registered_Success() {
        when(creditorClaimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));
        when(creditorClaimRepository.save(any(CreditorClaim.class))).thenReturn(mockClaim);

        CreditorClaimReviewRequest reviewRequest = new CreditorClaimReviewRequest();
        reviewRequest.setRegistrationStatus("REGISTERED");
        reviewRequest.setReviewOpinion("审核通过");
        reviewRequest.setClaimNatureManager("有担保债权");

        creditorClaimService.reviewClaim(1L, reviewRequest, 2L);

        verify(creditorClaimRepository, times(1)).save(any(CreditorClaim.class));
        assertEquals("REGISTERED", mockClaim.getRegistrationStatus());
        assertEquals("有担保债权", mockClaim.getClaimNatureManager());
        assertEquals(2L, mockClaim.getUpdateUserId());
    }

    @Test
    void testReviewClaim_Rejected_Success() {
        when(creditorClaimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));
        when(creditorClaimRepository.save(any(CreditorClaim.class))).thenReturn(mockClaim);

        CreditorClaimReviewRequest reviewRequest = new CreditorClaimReviewRequest();
        reviewRequest.setRegistrationStatus("REJECTED");
        reviewRequest.setReviewOpinion("审核驳回");

        creditorClaimService.reviewClaim(1L, reviewRequest, 2L);

        verify(creditorClaimRepository, times(1)).save(any(CreditorClaim.class));
        assertEquals("REJECTED", mockClaim.getRegistrationStatus());
        assertEquals(2L, mockClaim.getUpdateUserId());
    }

    @Test
    void testReviewClaim_NotPendingStatus() {
        mockClaim.setRegistrationStatus("REGISTERED");
        when(creditorClaimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));

        CreditorClaimReviewRequest reviewRequest = new CreditorClaimReviewRequest();
        reviewRequest.setRegistrationStatus("REGISTERED");
        reviewRequest.setReviewOpinion("审核通过");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            creditorClaimService.reviewClaim(1L, reviewRequest, 2L);
        });

        assertEquals("债权申报当前状态不允许审核", exception.getMessage());
        verify(creditorClaimRepository, never()).save(any(CreditorClaim.class));
    }

    @Test
    void testGetClaimReviewStatus_Success() {
        when(creditorClaimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));

        CreditorClaim result = creditorClaimService.getClaimReviewStatus(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDING", result.getRegistrationStatus());
        verify(creditorClaimRepository, times(1)).findById(1L);
    }
}
