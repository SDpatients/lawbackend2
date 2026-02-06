package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.SystemField;
import com.lawbackend2.lawbackend2.repository.SystemFieldRepository;
import com.lawbackend2.lawbackend2.service.SystemFieldService;
import com.lawbackend2.lawbackend2.dto.request.SystemFieldCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.SystemFieldUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.SystemFieldGroupResponse;
import com.lawbackend2.lawbackend2.dto.response.SystemFieldResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemFieldServiceImpl implements SystemFieldService {

    private final SystemFieldRepository systemFieldRepository;

    @Autowired
    public SystemFieldServiceImpl(SystemFieldRepository systemFieldRepository) {
        this.systemFieldRepository = systemFieldRepository;
    }

    @Override
    public SystemField createSystemField(SystemFieldCreateRequest request) {
        SystemField systemField = new SystemField();
        BeanUtils.copyProperties(request, systemField);
        return systemFieldRepository.save(systemField);
    }

    @Override
    public SystemField updateSystemField(Long id, SystemFieldUpdateRequest request) {
        SystemField systemField = systemFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("系统字段不存在"));
        BeanUtils.copyProperties(request, systemField);
        return systemFieldRepository.save(systemField);
    }

    @Override
    public void deleteSystemField(Long id) {
        systemFieldRepository.deleteById(id);
    }

    @Override
    public SystemField getSystemFieldById(Long id) {
        return systemFieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("系统字段不存在"));
    }

    @Override
    public List<SystemField> getAllSystemFields() {
        return systemFieldRepository.findAllByOrderByGroupNameAscSortOrderAsc();
    }

    @Override
    public List<SystemFieldGroupResponse> getSystemFieldGroups() {
        List<SystemField> systemFields = systemFieldRepository.findAllByOrderByGroupNameAscSortOrderAsc();
        Map<String, List<SystemFieldResponse>> groupMap = new HashMap<>();

        for (SystemField field : systemFields) {
            SystemFieldResponse fieldResponse = new SystemFieldResponse();
            BeanUtils.copyProperties(field, fieldResponse);

            List<SystemFieldResponse> fields = groupMap.computeIfAbsent(field.getGroupName(), k -> new ArrayList<>());
            fields.add(fieldResponse);
        }

        List<SystemFieldGroupResponse> groups = new ArrayList<>();
        for (Map.Entry<String, List<SystemFieldResponse>> entry : groupMap.entrySet()) {
            SystemFieldGroupResponse groupResponse = new SystemFieldGroupResponse();
            groupResponse.setGroup(entry.getKey());
            groupResponse.setFields(entry.getValue());
            groups.add(groupResponse);
        }

        return groups;
    }

    @Override
    public List<SystemField> getSystemFieldsByGroup(String groupName) {
        return systemFieldRepository.findByGroupNameOrderBySortOrderAsc(groupName);
    }
}
