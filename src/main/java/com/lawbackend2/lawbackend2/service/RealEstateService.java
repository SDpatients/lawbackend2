package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.RealEstateCreateRequest;
import com.lawbackend2.lawbackend2.entity.RealEstate;

public interface RealEstateService {

    Long createRealEstate(RealEstateCreateRequest request);

    PageResult<RealEstate> getRealEstateList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String estateType, String estateStatus, String managementStatus);

    RealEstate getRealEstateDetail(Long estateId);

    void updateRealEstate(Long estateId, RealEstateCreateRequest request);

    void deleteRealEstate(Long estateId);
}