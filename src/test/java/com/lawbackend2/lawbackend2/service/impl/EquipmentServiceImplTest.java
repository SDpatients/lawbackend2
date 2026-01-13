package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.EquipmentCreateRequest;
import com.lawbackend2.lawbackend2.entity.Equipment;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.EquipmentRepository;
import com.lawbackend2.lawbackend2.service.EquipmentService;
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
class EquipmentServiceImplTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_EQUIPMENT_ID = 1L;
    private static final String TEST_EQUIPMENT_TYPE = "PRODUCTION";
    private static final String TEST_EQUIPMENT_NAME = "测试设备";

    private EquipmentCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new EquipmentCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setEquipmentType(TEST_EQUIPMENT_TYPE);
        createRequest.setEquipmentName(TEST_EQUIPMENT_NAME);
        createRequest.setPurchaseCost(new BigDecimal("10000.00"));

        Equipment mockEquipment = new Equipment();
        mockEquipment.setId(TEST_EQUIPMENT_ID);
        mockEquipment.setCaseId(TEST_CASE_ID);
        mockEquipment.setEquipmentType(TEST_EQUIPMENT_TYPE);
        mockEquipment.setEquipmentName(TEST_EQUIPMENT_NAME);
        mockEquipment.setEquipmentStatus("NORMAL");
        mockEquipment.setManagementStatus("PENDING");

        when(equipmentRepository.save(any(Equipment.class))).thenReturn(mockEquipment);
        when(equipmentRepository.findById(TEST_EQUIPMENT_ID))
                .thenReturn(Optional.of(mockEquipment));
        when(equipmentRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateEquipment_Success() {
        Long equipmentId = equipmentService.createEquipment(createRequest);

        assertNotNull(equipmentId);
        assertEquals(TEST_EQUIPMENT_ID, equipmentId);
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    void testGetEquipmentDetail_Success() {
        Equipment equipment = equipmentService.getEquipmentDetail(TEST_EQUIPMENT_ID);

        assertNotNull(equipment);
        assertEquals(TEST_EQUIPMENT_ID, equipment.getId());
        assertEquals(TEST_CASE_ID, equipment.getCaseId());
        verify(equipmentRepository, times(1)).findById(TEST_EQUIPMENT_ID);
    }

    @Test
    void testGetEquipmentDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            equipmentService.getEquipmentDetail(999L);
        });

        assertEquals("设备不存在", exception.getMessage());
    }

    @Test
    void testUpdateEquipment_Success() {
        equipmentService.updateEquipment(TEST_EQUIPMENT_ID, createRequest);

        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    void testDeleteEquipment_Success() {
        equipmentService.deleteEquipment(TEST_EQUIPMENT_ID);

        verify(equipmentRepository, times(1)).delete(any(Equipment.class));
    }
}