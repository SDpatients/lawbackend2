package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.PropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.Property;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.PropertyRepository;
import com.lawbackend2.lawbackend2.service.PropertyService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Long createProperty(PropertyCreateRequest request) {
        Property property = new Property();
        BeanUtils.copyProperties(request, property);
        property.setPropertyNo(generatePropertyNo());
        property.setPropertyStatus("NORMAL");
        property.setManagementStatus("PENDING");
        property.setStatus("ACTIVE");

        Property saved = propertyRepository.save(property);
        return saved.getId();
    }

    @Override
    public PageResult<Property> getPropertyList(Integer pageNum, Integer pageSize, Long caseId, String propertyType, String propertyStatus, String managementStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Property> page = propertyRepository.findByConditions(caseId, propertyType, propertyStatus, managementStatus, pageable);

        PageResult<Property> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public Property getPropertyDetail(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BusinessException("财产不存在"));
    }

    @Override
    public void updateProperty(Long propertyId, PropertyCreateRequest request) {
        Property property = getPropertyDetail(propertyId);
        BeanUtils.copyProperties(request, property, "id", "propertyNo", "caseId", "caseName");
        propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(Long propertyId) {
        Property property = getPropertyDetail(propertyId);
        propertyRepository.delete(property);
    }

    private String generatePropertyNo() {
        return "PROP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}