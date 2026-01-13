package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.IntellectualPropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.IntellectualProperty;

public interface IntellectualPropertyService {

    Long createIntellectualProperty(IntellectualPropertyCreateRequest request);

    PageResult<IntellectualProperty> getIntellectualPropertyList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String ipType, String ipStatus, String managementStatus);

    IntellectualProperty getIntellectualPropertyDetail(Long ipId);

    void updateIntellectualProperty(Long ipId, IntellectualPropertyCreateRequest request);

    void deleteIntellectualProperty(Long ipId);
}