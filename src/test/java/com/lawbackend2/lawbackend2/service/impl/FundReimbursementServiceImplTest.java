package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.FundReimbursementApprovalRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundReimbursementPaymentRequest;
import com.lawbackend2.lawbackend2.entity.FundReimbursement;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.FundReimbursementRepository;
import com.lawbackend2.lawbackend2.service.FundReimbursementService;
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
class FundReimbursementServiceImplTest {

    @Mock
    private FundReimbursementRepository fundReimbursementRepository;

    @InjectMocks
    private FundReimbursementServiceImpl fundReimbursementService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_REIMBURSEMENT_ID = 1L;
    private static final String TEST_REIMBURSEMENT_TYPE = "TRAVEL";
    private static final String TEST_REIMBURSEMENT_NAME = "差旅费报销";
    private static final Long TEST_APPLICANT_ID = 1L;
    private static final BigDecimal TEST_AMOUNT = new BigDecimal("5000.00");

    private FundReimbursementCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new FundReimbursementCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setReimbursementType(TEST_REIMBURSEMENT_TYPE);
        createRequest.setReimbursementName(TEST_REIMBURSEMENT_NAME);
        createRequest.setApplicantId(TEST_APPLICANT_ID);
        createRequest.setApplicantName("测试申请人");
        createRequest.setAppliedAmount(TEST_AMOUNT);

        FundReimbursement mockReimbursement = new FundReimbursement();
        mockReimbursement.setId(TEST_REIMBURSEMENT_ID);
        mockReimbursement.setCaseId(TEST_CASE_ID);
        mockReimbursement.setReimbursementType(TEST_REIMBURSEMENT_TYPE);
        mockReimbursement.setReimbursementName(TEST_REIMBURSEMENT_NAME);
        mockReimbursement.setApplicantId(TEST_APPLICANT_ID);
        mockReimbursement.setAppliedAmount(TEST_AMOUNT);
        mockReimbursement.setApprovalStatus("PENDING");
        mockReimbursement.setPaymentStatus("UNPAID");

        when(fundReimbursementRepository.save(any(FundReimbursement.class))).thenReturn(mockReimbursement);
        when(fundReimbursementRepository.findById(TEST_REIMBURSEMENT_ID))
                .thenReturn(Optional.of(mockReimbursement));
        when(fundReimbursementRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateFundReimbursement_Success() {
        Long reimbursementId = fundReimbursementService.createFundReimbursement(createRequest);

        assertNotNull(reimbursementId);
        assertEquals(TEST_REIMBURSEMENT_ID, reimbursementId);
        verify(fundReimbursementRepository, times(1)).save(any(FundReimbursement.class));
    }

    @Test
    void testGetFundReimbursementDetail_Success() {
        FundReimbursement reimbursement = fundReimbursementService.getFundReimbursementDetail(TEST_REIMBURSEMENT_ID);

        assertNotNull(reimbursement);
        assertEquals(TEST_REIMBURSEMENT_ID, reimbursement.getId());
        assertEquals(TEST_CASE_ID, reimbursement.getCaseId());
        verify(fundReimbursementRepository, times(1)).findById(TEST_REIMBURSEMENT_ID);
    }

    @Test
    void testGetFundReimbursementDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            fundReimbursementService.getFundReimbursementDetail(999L);
        });

        assertEquals("费用报销不存在", exception.getMessage());
    }

    @Test
    void testApproveFundReimbursement_Success() {
        FundReimbursementApprovalRequest approvalRequest = new FundReimbursementApprovalRequest();
        approvalRequest.setApprovalStatus("APPROVED");
        approvalRequest.setApprovedAmount(TEST_AMOUNT);

        fundReimbursementService.approveFundReimbursement(TEST_REIMBURSEMENT_ID, approvalRequest);

        verify(fundReimbursementRepository, times(1)).save(any(FundReimbursement.class));
    }

    @Test
    void testPayFundReimbursement_Success() {
        FundReimbursementPaymentRequest paymentRequest = new FundReimbursementPaymentRequest();
        paymentRequest.setPaymentStatus("PAID");
        paymentRequest.setReimbursedAmount(TEST_AMOUNT);

        fundReimbursementService.payFundReimbursement(TEST_REIMBURSEMENT_ID, paymentRequest);

        verify(fundReimbursementRepository, times(1)).save(any(FundReimbursement.class));
    }

    @Test
    void testDeleteFundReimbursement_Success() {
        fundReimbursementService.deleteFundReimbursement(TEST_REIMBURSEMENT_ID);

        verify(fundReimbursementRepository, times(1)).delete(any(FundReimbursement.class));
    }
}