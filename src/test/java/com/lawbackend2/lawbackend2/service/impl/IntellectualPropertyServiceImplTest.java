package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.IntellectualPropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.IntellectualProperty;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.IntellectualPropertyRepository;
import com.lawbackend2.lawbackend2.service.IntellectualPropertyService;
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
class IntellectualPropertyServiceImplTest {

    @Mock
    private IntellectualPropertyRepository intellectualPropertyRepository;

    @InjectMocks
    private IntellectualPropertyServiceImpl intellectualPropertyService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_IP_ID = 1L;
    private static final String TEST_IP_TYPE = "PATENT";
    private static final String TEST_IP_NAME = "测试知识产权";

    private IntellectualPropertyCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new IntellectualPropertyCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setIpType(TEST_IP_TYPE);
        createRequest.setIpName(TEST_IP_NAME);
        createRequest.setRegistrationCost(new BigDecimal("10000.00"));

        IntellectualProperty mockIp = new IntellectualProperty();
        mockIp.setId(TEST_IP_ID);
        mockIp.setCaseId(TEST_CASE_ID);
        mockIp.setIpType(TEST_IP_TYPE);
        mockIp.setIpName(TEST_IP_NAME);
        mockIp.setIpStatus("NORMAL");
        mockIp.setManagementStatus("PENDING");

        when(intellectualPropertyRepository.save(any(IntellectualProperty.class))).thenReturn(mockIp);
        when(intellectualPropertyRepository.findById(TEST_IP_ID))
                .thenReturn(Optional.of(mockIp));
        when(intellectualPropertyRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateIntellectualProperty_Success() {
        Long ipId = intellectualPropertyService.createIntellectualProperty(createRequest);

        assertNotNull(ipId);
        assertEquals(TEST_IP_ID, ipId);
        verify(intellectualPropertyRepository, times(1)).save(any(IntellectualProperty.class));
    }

    @Test
    void testGetIntellectualPropertyDetail_Success() {
        IntellectualProperty ip = intellectualPropertyService.getIntellectualPropertyDetail(TEST_IP_ID);

        assertNotNull(ip);
        assertEquals(TEST_IP_ID, ip.getId());
        assertEquals(TEST_CASE_ID, ip.getCaseId());
        verify(intellectualPropertyRepository, times(1)).findById(TEST_IP_ID);
    }

    @Test
    void testGetIntellectualPropertyDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            intellectualPropertyService.getIntellectualPropertyDetail(999L);
        });

        assertEquals("知识产权不存在", exception.getMessage());
    }

    @Test
    void testUpdateIntellectualProperty_Success() {
        intellectualPropertyService.updateIntellectualProperty(TEST_IP_ID, createRequest);

        verify(intellectualPropertyRepository, times(1)).save(any(IntellectualProperty.class));
    }

    @Test
    void testDeleteIntellectualProperty_Success() {
        intellectualPropertyService.deleteIntellectualProperty(TEST_IP_ID);

        verify(intellectualPropertyRepository, times(1)).delete(any(IntellectualProperty.class));
    }
}