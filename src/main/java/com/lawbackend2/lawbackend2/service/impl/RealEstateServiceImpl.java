package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.RealEstateCreateRequest;
import com.lawbackend2.lawbackend2.entity.RealEstate;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.RealEstateRepository;
import com.lawbackend2.lawbackend2.service.RealEstateService;
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
public class RealEstateServiceImpl implements RealEstateService {

    private final RealEstateRepository realEstateRepository;

    public RealEstateServiceImpl(RealEstateRepository realEstateRepository) {
        this.realEstateRepository = realEstateRepository;
    }

    @Override
    public Long createRealEstate(RealEstateCreateRequest request) {
        RealEstate estate = new RealEstate();
        BeanUtils.copyProperties(request, estate);
        estate.setEstateNo(generateEstateNo());
        estate.setEstateStatus("NORMAL");
        estate.setManagementStatus("PENDING");
        estate.setStatus("ACTIVE");

        RealEstate saved = realEstateRepository.save(estate);
        return saved.getId();
    }

    @Override
    public PageResult<RealEstate> getRealEstateList(Integer pageNum, Integer pageSize, Long caseId, Long propertyId, String estateType, String estateStatus, String managementStatus) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<RealEstate> page = realEstateRepository.findByConditions(caseId, estateType, estateStatus, managementStatus, pageable);

        PageResult<RealEstate> result = new PageResult<>();
        result.setTotal(page.getTotalElements());
        result.setList(page.getContent());
        return result;
    }

    @Override
    public RealEstate getRealEstateDetail(Long estateId) {
        return realEstateRepository.findById(estateId)
                .orElseThrow(() -> new BusinessException("房产不存在"));
    }

    @Override
    public void updateRealEstate(Long estateId, RealEstateCreateRequest request) {
        RealEstate estate = getRealEstateDetail(estateId);
        BeanUtils.copyProperties(request, estate, "id", "estateNo", "caseId", "caseName", "propertyId");
        realEstateRepository.save(estate);
    }

    @Override
    public void deleteRealEstate(Long estateId) {
        RealEstate estate = getRealEstateDetail(estateId);
        realEstateRepository.delete(estate);
    }

    private String generateEstateNo() {
        return "EST" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}