package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.AdministratorCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorStaffCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Administrator;
import com.lawbackend2.lawbackend2.entity.AdministratorStaff;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.AdministratorRepository;
import com.lawbackend2.lawbackend2.repository.AdministratorStaffRepository;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.AdministratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AdministratorServiceImpl implements AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final AdministratorStaffRepository administratorStaffRepository;
    private final BankruptCaseRepository bankruptCaseRepository;

    public AdministratorServiceImpl(AdministratorRepository administratorRepository,
                                     AdministratorStaffRepository administratorStaffRepository,
                                     BankruptCaseRepository bankruptCaseRepository) {
        this.administratorRepository = administratorRepository;
        this.administratorStaffRepository = administratorStaffRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Administrator createAdministrator(AdministratorCreateRequest request, Long userId) {
        log.info("创建管理人信息, 案件ID: {}, 创建人ID: {}", request.getCaseId(), userId);

        BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new BusinessException("案件不存在"));

        if (request.getResponsiblePersonId() != null) {
            AdministratorStaff staff = administratorStaffRepository.findById(request.getResponsiblePersonId())
                    .orElseThrow(() -> new BusinessException("员工不存在"));
        }

        Administrator administrator = new Administrator();
        BeanUtils.copyProperties(request, administrator);
        administrator.setCreateUserId(userId);
        administrator.setUpdateUserId(userId);

        Administrator saved = administratorRepository.save(administrator);
        log.info("管理人信息创建成功, ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Administrator getAdministratorById(Long administratorId) {
        log.debug("查询管理人信息, ID: {}", administratorId);
        return administratorRepository.findById(administratorId)
                .orElseThrow(() -> new BusinessException("管理人信息不存在"));
    }

    @Override
    public List<Administrator> getAdministratorList(Integer pageNum, Integer pageSize, Long caseId) {
        log.debug("查询管理人列表, pageNum: {}, pageSize: {}, caseId: {}", pageNum, pageSize, caseId);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Administrator> page;
        if (caseId != null) {
            page = administratorRepository.findByCaseId(caseId, pageable);
        } else {
            page = administratorRepository.findAll(pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getAdministratorCount(Long caseId) {
        if (caseId != null) {
            return administratorRepository.findByCaseId(caseId, Pageable.unpaged()).getTotalElements();
        } else {
            return administratorRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Administrator updateAdministrator(Long administratorId, AdministratorUpdateRequest request) {
        log.info("更新管理人信息, ID: {}", administratorId);

        Administrator administrator = getAdministratorById(administratorId);

        if (request.getContactPhone() != null) {
            administrator.setContactPhone(request.getContactPhone());
        }
        if (request.getContactEmail() != null) {
            administrator.setContactEmail(request.getContactEmail());
        }
        if (request.getOfficeAddress() != null) {
            administrator.setOfficeAddress(request.getOfficeAddress());
        }

        Administrator updated = administratorRepository.save(administrator);
        log.info("管理人信息更新成功, ID: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdministratorStaff createAdministratorStaff(AdministratorStaffCreateRequest request, Long userId) {
        log.info("创建管理人员工, 管理人ID: {}, 创建人ID: {}", request.getAdministratorId(), userId);

        Administrator administrator = administratorRepository.findById(request.getAdministratorId())
                .orElseThrow(() -> new BusinessException("管理人不存在"));

        AdministratorStaff staff = new AdministratorStaff();
        BeanUtils.copyProperties(request, staff);
        staff.setCreateUserId(userId);
        staff.setUpdateUserId(userId);

        AdministratorStaff saved = administratorStaffRepository.save(staff);
        log.info("管理人员工创建成功, ID: {}", saved.getId());
        return saved;
    }

    @Override
    public List<AdministratorStaff> getAdministratorStaffList(Long administratorId) {
        log.debug("查询管理人员工列表, 管理人ID: {}", administratorId);
        return administratorStaffRepository.findByAdministratorId(administratorId);
    }

    @Override
    public AdministratorStaff getAdministratorStaffById(Long staffId) {
        log.debug("查询管理人员工信息, ID: {}", staffId);
        return administratorStaffRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException("管理人员工信息不存在"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdministrator(Long administratorId) {
        log.info("删除管理人信息, ID: {}", administratorId);
        if (!administratorRepository.existsById(administratorId)) {
            throw new BusinessException("管理人信息不存在");
        }
        administratorRepository.deleteById(administratorId);
        log.info("管理人信息删除成功, ID: {}", administratorId);
    }
}
