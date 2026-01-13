package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.EquipmentCreateRequest;
import com.lawbackend2.lawbackend2.entity.Equipment;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.EquipmentRepository;
import com.lawbackend2.lawbackend2.service.EquipmentService;
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
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public Long createEquipment(EquipmentCreateRequest request) {
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(request, equipment);
        equipment.setEquipmentNo(generateEquipmentNo());
        equipment.setEquipmentStatus("NORMAL");
        equipment.setManagementStatus("PENDING");
        equipment.setStatus("ACTIVE");

        Equipment saved = equipmentRepository.save(equipment);
        return saved.getId();
    }

    @Override
    public PageResult<Equipment> getEquipmentList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String equipmentType, String equipmentStatus, String managementStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Equipment> page = equipmentRepository.findByConditions(caseId, equipmentType, equipmentStatus, managementStatus, pageable);

        PageResult<Equipment> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public Equipment getEquipmentDetail(Long equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new BusinessException("设备不存在"));
    }

    @Override
    public void updateEquipment(Long equipmentId, EquipmentCreateRequest request) {
        Equipment equipment = getEquipmentDetail(equipmentId);
        BeanUtils.copyProperties(request, equipment, "id", "equipmentNo", "caseId", "caseName", "propertyId");
        equipmentRepository.save(equipment);
    }

    @Override
    public void deleteEquipment(Long equipmentId) {
        Equipment equipment = getEquipmentDetail(equipmentId);
        equipmentRepository.delete(equipment);
    }

    private String generateEquipmentNo() {
        return "EQP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}