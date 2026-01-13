package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.PropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.Property;

public interface PropertyService {

    Long createProperty(PropertyCreateRequest request);

    PageResult<Property> getPropertyList(Integer pageNum, Integer pageSize, Long caseId, String propertyType, String propertyStatus, String managementStatus);

    Property getPropertyDetail(Long propertyId);

    void updateProperty(Long propertyId, PropertyCreateRequest request);

    void deleteProperty(Long propertyId);
}