package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.EquipmentCreateRequest;
import com.lawbackend2.lawbackend2.entity.Equipment;

public interface EquipmentService {

    Long createEquipment(EquipmentCreateRequest request);

    PageResult<Equipment> getEquipmentList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String equipmentType, String equipmentStatus, String managementStatus);

    Equipment getEquipmentDetail(Long equipmentId);

    void updateEquipment(Long equipmentId, EquipmentCreateRequest request);

    void deleteEquipment(Long equipmentId);
}