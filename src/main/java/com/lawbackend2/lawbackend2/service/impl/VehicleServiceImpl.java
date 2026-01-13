package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.VehicleCreateRequest;
import com.lawbackend2.lawbackend2.entity.Vehicle;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.VehicleRepository;
import com.lawbackend2.lawbackend2.service.VehicleService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Long createVehicle(VehicleCreateRequest request) {
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(request, vehicle);
        vehicle.setVehicleNo(generateVehicleNo());
        vehicle.setVehicleStatus("NORMAL");
        vehicle.setManagementStatus("PENDING");
        vehicle.setStatus("ACTIVE");

        Vehicle saved = vehicleRepository.save(vehicle);
        return saved.getId();
    }

    @Override
    public PageResult<Vehicle> getVehicleList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String vehicleType, String vehicleStatus, String managementStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Vehicle> page = vehicleRepository.findByConditions(caseId, vehicleType, vehicleStatus, managementStatus, pageable);

        PageResult<Vehicle> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public Vehicle getVehicleDetail(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new BusinessException("车辆不存在"));
    }

    @Override
    public void updateVehicle(Long vehicleId, VehicleCreateRequest request) {
        Vehicle vehicle = getVehicleDetail(vehicleId);
        BeanUtils.copyProperties(request, vehicle, "id", "vehicleNo", "caseId", "caseName", "propertyId");
        vehicleRepository.save(vehicle);
    }

    @Override
    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = getVehicleDetail(vehicleId);
        vehicleRepository.delete(vehicle);
    }

    private String generateVehicleNo() {
        return "VEH" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}