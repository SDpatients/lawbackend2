package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.PropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.Property;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.PropertyRepository;
import com.lawbackend2.lawbackend2.service.PropertyService;
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
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_PROPERTY_ID = 1L;
    private static final String TEST_PROPERTY_TYPE = "REAL_ESTATE";
    private static final String TEST_PROPERTY_NAME = "测试财产";

    private PropertyCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new PropertyCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setPropertyType(TEST_PROPERTY_TYPE);
        createRequest.setPropertyName(TEST_PROPERTY_NAME);
        createRequest.setOwnerName("测试所有权人");
        createRequest.setAcquisitionCost(new BigDecimal("100000.00"));

        Property mockProperty = new Property();
        mockProperty.setId(TEST_PROPERTY_ID);
        mockProperty.setCaseId(TEST_CASE_ID);
        mockProperty.setPropertyType(TEST_PROPERTY_TYPE);
        mockProperty.setPropertyName(TEST_PROPERTY_NAME);
        mockProperty.setPropertyStatus("NORMAL");
        mockProperty.setManagementStatus("PENDING");

        when(propertyRepository.save(any(Property.class))).thenReturn(mockProperty);
        when(propertyRepository.findById(TEST_PROPERTY_ID))
                .thenReturn(Optional.of(mockProperty));
        when(propertyRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateProperty_Success() {
        Long propertyId = propertyService.createProperty(createRequest);

        assertNotNull(propertyId);
        assertEquals(TEST_PROPERTY_ID, propertyId);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testGetPropertyDetail_Success() {
        Property property = propertyService.getPropertyDetail(TEST_PROPERTY_ID);

        assertNotNull(property);
        assertEquals(TEST_PROPERTY_ID, property.getId());
        assertEquals(TEST_CASE_ID, property.getCaseId());
        verify(propertyRepository, times(1)).findById(TEST_PROPERTY_ID);
    }

    @Test
    void testGetPropertyDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            propertyService.getPropertyDetail(999L);
        });

        assertEquals("财产不存在", exception.getMessage());
    }

    @Test
    void testUpdateProperty_Success() {
        propertyService.updateProperty(TEST_PROPERTY_ID, createRequest);

        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testDeleteProperty_Success() {
        propertyService.deleteProperty(TEST_PROPERTY_ID);

        verify(propertyRepository, times(1)).delete(any(Property.class));
    }
}