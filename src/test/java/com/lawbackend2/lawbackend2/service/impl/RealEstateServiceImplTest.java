package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.RealEstateCreateRequest;
import com.lawbackend2.lawbackend2.entity.RealEstate;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.RealEstateRepository;
import com.lawbackend2.lawbackend2.service.RealEstateService;
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
class RealEstateServiceImplTest {

    @Mock
    private RealEstateRepository realEstateRepository;

    @InjectMocks
    private RealEstateServiceImpl realEstateService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_ESTATE_ID = 1L;
    private static final String TEST_ESTATE_TYPE = "RESIDENTIAL";
    private static final String TEST_ESTATE_NAME = "测试房产";

    private RealEstateCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new RealEstateCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setEstateType(TEST_ESTATE_TYPE);
        createRequest.setEstateName(TEST_ESTATE_NAME);
        createRequest.setEstateAddress("测试地址");
        createRequest.setBuildingArea(new BigDecimal("100.00"));

        RealEstate mockEstate = new RealEstate();
        mockEstate.setId(TEST_ESTATE_ID);
        mockEstate.setCaseId(TEST_CASE_ID);
        mockEstate.setEstateType(TEST_ESTATE_TYPE);
        mockEstate.setEstateName(TEST_ESTATE_NAME);
        mockEstate.setEstateStatus("NORMAL");
        mockEstate.setManagementStatus("PENDING");

        when(realEstateRepository.save(any(RealEstate.class))).thenReturn(mockEstate);
        when(realEstateRepository.findById(TEST_ESTATE_ID))
                .thenReturn(Optional.of(mockEstate));
        when(realEstateRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateRealEstate_Success() {
        Long estateId = realEstateService.createRealEstate(createRequest);

        assertNotNull(estateId);
        assertEquals(TEST_ESTATE_ID, estateId);
        verify(realEstateRepository, times(1)).save(any(RealEstate.class));
    }

    @Test
    void testGetRealEstateDetail_Success() {
        RealEstate estate = realEstateService.getRealEstateDetail(TEST_ESTATE_ID);

        assertNotNull(estate);
        assertEquals(TEST_ESTATE_ID, estate.getId());
        assertEquals(TEST_CASE_ID, estate.getCaseId());
        verify(realEstateRepository, times(1)).findById(TEST_ESTATE_ID);
    }

    @Test
    void testGetRealEstateDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            realEstateService.getRealEstateDetail(999L);
        });

        assertEquals("房产不存在", exception.getMessage());
    }

    @Test
    void testUpdateRealEstate_Success() {
        realEstateService.updateRealEstate(TEST_ESTATE_ID, createRequest);

        verify(realEstateRepository, times(1)).save(any(RealEstate.class));
    }

    @Test
    void testDeleteRealEstate_Success() {
        realEstateService.deleteRealEstate(TEST_ESTATE_ID);

        verify(realEstateRepository, times(1)).delete(any(RealEstate.class));
    }
}