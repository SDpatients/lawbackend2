package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.VehicleCreateRequest;
import com.lawbackend2.lawbackend2.entity.Vehicle;

public interface VehicleService {

    Long createVehicle(VehicleCreateRequest request);

    PageResult<Vehicle> getVehicleList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String vehicleType, String vehicleStatus, String managementStatus);

    Vehicle getVehicleDetail(Long vehicleId);

    void updateVehicle(Long vehicleId, VehicleCreateRequest request);

    void deleteVehicle(Long vehicleId);
}