package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.SystemField;
import com.lawbackend2.lawbackend2.dto.request.SystemFieldCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.SystemFieldUpdateRequest;
import com.lawbackend2.lawbackend2.dto.response.SystemFieldGroupResponse;

import java.util.List;

public interface SystemFieldService {
    SystemField createSystemField(SystemFieldCreateRequest request);
    SystemField updateSystemField(Long id, SystemFieldUpdateRequest request);
    void deleteSystemField(Long id);
    SystemField getSystemFieldById(Long id);
    List<SystemField> getAllSystemFields();
    List<SystemFieldGroupResponse> getSystemFieldGroups();
    List<SystemField> getSystemFieldsByGroup(String groupName);
}
