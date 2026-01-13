package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.VehicleCreateRequest;
import com.lawbackend2.lawbackend2.entity.Vehicle;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.VehicleRepository;
import com.lawbackend2.lawbackend2.service.VehicleService;
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
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private static final Long TEST_CASE_ID = 1L;
    private static final Long TEST_VEHICLE_ID = 1L;
    private static final String TEST_VEHICLE_TYPE = "CAR";
    private static final String TEST_VEHICLE_NAME = "测试车辆";

    private VehicleCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new VehicleCreateRequest();
        createRequest.setCaseId(TEST_CASE_ID);
        createRequest.setCaseName("测试案件");
        createRequest.setVehicleType(TEST_VEHICLE_TYPE);
        createRequest.setVehicleName(TEST_VEHICLE_NAME);
        createRequest.setLicensePlate("京A12345");
        createRequest.setAcquisitionCost(new BigDecimal("50000.00"));

        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setId(TEST_VEHICLE_ID);
        mockVehicle.setCaseId(TEST_CASE_ID);
        mockVehicle.setVehicleType(TEST_VEHICLE_TYPE);
        mockVehicle.setVehicleName(TEST_VEHICLE_NAME);
        mockVehicle.setVehicleStatus("NORMAL");
        mockVehicle.setManagementStatus("PENDING");

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(mockVehicle);
        when(vehicleRepository.findById(TEST_VEHICLE_ID))
                .thenReturn(Optional.of(mockVehicle));
        when(vehicleRepository.findById(999L))
                .thenReturn(Optional.empty());
    }

    @Test
    void testCreateVehicle_Success() {
        Long vehicleId = vehicleService.createVehicle(createRequest);

        assertNotNull(vehicleId);
        assertEquals(TEST_VEHICLE_ID, vehicleId);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testGetVehicleDetail_Success() {
        Vehicle vehicle = vehicleService.getVehicleDetail(TEST_VEHICLE_ID);

        assertNotNull(vehicle);
        assertEquals(TEST_VEHICLE_ID, vehicle.getId());
        assertEquals(TEST_CASE_ID, vehicle.getCaseId());
        verify(vehicleRepository, times(1)).findById(TEST_VEHICLE_ID);
    }

    @Test
    void testGetVehicleDetail_NotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            vehicleService.getVehicleDetail(999L);
        });

        assertEquals("车辆不存在", exception.getMessage());
    }

    @Test
    void testUpdateVehicle_Success() {
        vehicleService.updateVehicle(TEST_VEHICLE_ID, createRequest);

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testDeleteVehicle_Success() {
        vehicleService.deleteVehicle(TEST_VEHICLE_ID);

        verify(vehicleRepository, times(1)).delete(any(Vehicle.class));
    }
}