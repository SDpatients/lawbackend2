package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CreditorCreateRequest;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import com.lawbackend2.lawbackend2.service.impl.CreditorInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditorInfoServiceTest {

    @Mock
    private CreditorInfoRepository creditorInfoRepository;

    @InjectMocks
    private CreditorInfoServiceImpl creditorInfoService;

    private CreditorCreateRequest createRequest;
    private CreditorInfo mockCreditor;

    @BeforeEach
    void setUp() {
        createRequest = new CreditorCreateRequest();
        createRequest.setCaseId(1L);
        createRequest.setCreditorName("测试债权人");
        createRequest.setCreditorType("企业");
        createRequest.setContactPhone("13800138000");
        createRequest.setRegisteredCapital(new BigDecimal("1000000.00"));

        mockCreditor = new CreditorInfo();
        mockCreditor.setId(1L);
        mockCreditor.setCaseId(1L);
        mockCreditor.setCreditorName("测试债权人");
        mockCreditor.setCreditorType("企业");
        mockCreditor.setContactPhone("13800138000");
    }

    @Test
    void testCreateCreditor_Success() {
        when(creditorInfoRepository.save(any(CreditorInfo.class))).thenReturn(mockCreditor);

        CreditorInfo result = creditorInfoService.createCreditor(createRequest, 1L);

        assertNotNull(result);
        assertEquals("测试债权人", result.getCreditorName());
        assertEquals("企业", result.getCreditorType());
        verify(creditorInfoRepository, times(1)).save(any(CreditorInfo.class));
    }

    @Test
    void testGetCreditorById_Success() {
        when(creditorInfoRepository.findById(1L)).thenReturn(Optional.of(mockCreditor));

        CreditorInfo result = creditorInfoService.getCreditorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试债权人", result.getCreditorName());
        verify(creditorInfoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCreditorById_NotFound() {
        when(creditorInfoRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            creditorInfoService.getCreditorById(999L);
        });

        assertEquals("债权人不存在", exception.getMessage());
    }

    @Test
    void testGetCreditorList() {
        List<CreditorInfo> creditors = Arrays.asList(mockCreditor);
        Page<CreditorInfo> page = new PageImpl<>(creditors);
        when(creditorInfoRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(page);

        List<CreditorInfo> result = creditorInfoService.getCreditorList(1, 10, 1L, null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试债权人", result.get(0).getCreditorName());
    }
}
