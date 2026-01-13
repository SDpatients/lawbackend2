package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.IntellectualPropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.IntellectualProperty;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.IntellectualPropertyRepository;
import com.lawbackend2.lawbackend2.service.IntellectualPropertyService;
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
public class IntellectualPropertyServiceImpl implements IntellectualPropertyService {

    private final IntellectualPropertyRepository intellectualPropertyRepository;

    public IntellectualPropertyServiceImpl(IntellectualPropertyRepository intellectualPropertyRepository) {
        this.intellectualPropertyRepository = intellectualPropertyRepository;
    }

    @Override
    public Long createIntellectualProperty(IntellectualPropertyCreateRequest request) {
        IntellectualProperty ip = new IntellectualProperty();
        BeanUtils.copyProperties(request, ip);
        ip.setIpNo(generateIpNo());
        ip.setIpStatus("NORMAL");
        ip.setManagementStatus("PENDING");
        ip.setStatus("ACTIVE");

        IntellectualProperty saved = intellectualPropertyRepository.save(ip);
        return saved.getId();
    }

    @Override
    public PageResult<IntellectualProperty> getIntellectualPropertyList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String ipType, String ipStatus, String managementStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<IntellectualProperty> page = intellectualPropertyRepository.findByConditions(caseId, ipType, ipStatus, managementStatus, pageable);

        PageResult<IntellectualProperty> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public IntellectualProperty getIntellectualPropertyDetail(Long ipId) {
        return intellectualPropertyRepository.findById(ipId)
                .orElseThrow(() -> new BusinessException("知识产权不存在"));
    }

    @Override
    public void updateIntellectualProperty(Long ipId, IntellectualPropertyCreateRequest request) {
        IntellectualProperty ip = getIntellectualPropertyDetail(ipId);
        BeanUtils.copyProperties(request, ip, "id", "ipNo", "caseId", "caseName", "propertyId");
        intellectualPropertyRepository.save(ip);
    }

    @Override
    public void deleteIntellectualProperty(Long ipId) {
        IntellectualProperty ip = getIntellectualPropertyDetail(ipId);
        intellectualPropertyRepository.delete(ip);
    }

    private String generateIpNo() {
        return "IP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}