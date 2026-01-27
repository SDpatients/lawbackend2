package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.CreditorCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorInfoResponse;
import com.lawbackend2.lawbackend2.dto.CreditorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
import com.lawbackend2.lawbackend2.service.CreditorInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreditorInfoServiceImpl implements CreditorInfoService {

    private final CreditorInfoRepository creditorInfoRepository;
    private final BankruptCaseService bankruptCaseService;
    private final WorkTeamMemberRepository workTeamMemberRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public CreditorInfoServiceImpl(CreditorInfoRepository creditorInfoRepository, BankruptCaseService bankruptCaseService, WorkTeamMemberRepository workTeamMemberRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.creditorInfoRepository = creditorInfoRepository;
        this.bankruptCaseService = bankruptCaseService;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreditorInfo createCreditor(CreditorCreateRequest request, Long userId) {
        CreditorInfo creditorInfo = new CreditorInfo();
        BeanUtils.copyProperties(request, creditorInfo);
        creditorInfo.setCreateUserId(userId);
        creditorInfo.setUpdateUserId(userId);

        return creditorInfoRepository.save(creditorInfo);
    }

    @Override
    public CreditorInfo getCreditorById(Long creditorId) {
        return creditorInfoRepository.findById(creditorId)
                .orElseThrow(() -> new BusinessException("债权人不存在"));
    }

    @Override
    public CreditorInfoResponse getCreditorByIdWithCaseInfo(Long creditorId, Long userId) {
        CreditorInfo creditorInfo = getCreditorById(creditorId);
        
        checkPermission(creditorInfo, userId);
        
        CreditorInfoResponse response = new CreditorInfoResponse();
        BeanUtils.copyProperties(creditorInfo, response);
        
        if (creditorInfo.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseService.getCaseById(creditorInfo.getCaseId());
            if (bankruptCase != null) {
                response.setCaseNumber(bankruptCase.getCaseNumber());
                response.setCaseName(bankruptCase.getCaseName());
            }
        }
        
        return response;
    }

    @Override
    public List<CreditorInfo> getCreditorList(Integer pageNum, Integer pageSize, Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<CreditorInfo> spec = buildSpecification(caseId, creditorType, creditorName, idNumber, legalRepresentative);
        Page<CreditorInfo> page = creditorInfoRepository.findAll(spec, pageable);

        return page.getContent();
    }

    @Override
    public Long getCreditorCount(Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative) {
        Specification<CreditorInfo> spec = buildSpecification(caseId, creditorType, creditorName, idNumber, legalRepresentative);
        return creditorInfoRepository.count(spec);
    }

    private Specification<CreditorInfo> buildSpecification(Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (caseId != null) {
                predicates.add(cb.equal(root.get("caseId"), caseId));
            }

            if (creditorType != null && !creditorType.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("creditorType"), creditorType));
            }

            if (creditorName != null && !creditorName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("creditorName"), "%" + creditorName + "%"));
            }

            if (idNumber != null && !idNumber.trim().isEmpty()) {
                predicates.add(cb.like(root.get("idNumber"), "%" + idNumber + "%"));
            }

            if (legalRepresentative != null && !legalRepresentative.trim().isEmpty()) {
                predicates.add(cb.like(root.get("legalRepresentative"), "%" + legalRepresentative + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<CreditorInfo> buildSpecificationWithPermission(Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (caseId != null) {
                predicates.add(cb.equal(root.get("caseId"), caseId));
            }

            if (creditorType != null && !creditorType.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("creditorType"), creditorType));
            }

            if (creditorName != null && !creditorName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("creditorName"), "%" + creditorName + "%"));
            }

            if (idNumber != null && !idNumber.trim().isEmpty()) {
                predicates.add(cb.like(root.get("idNumber"), "%" + idNumber + "%"));
            }

            if (legalRepresentative != null && !legalRepresentative.trim().isEmpty()) {
                predicates.add(cb.like(root.get("legalRepresentative"), "%" + legalRepresentative + "%"));
            }

            if (!isAdminOrSuperAdmin(userId)) {
                List<Long> accessibleCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
                predicates.add(cb.or(
                    cb.equal(root.get("createUserId"), userId),
                    root.get("caseId").in(accessibleCaseIds)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void checkPermission(CreditorInfo creditorInfo, Long userId) {
        if (isAdminOrSuperAdmin(userId)) {
            return;
        }
        
        if (creditorInfo.getCreateUserId().equals(userId)) {
            return;
        }
        
        if (creditorInfo.getCaseId() != null) {
            List<Long> accessibleCaseIds = workTeamMemberRepository.findCaseIdsByUserId(userId);
            if (accessibleCaseIds.contains(creditorInfo.getCaseId())) {
                return;
            }
        }
        
        throw new BusinessException("无权访问该债权人信息");
    }

    private boolean isAdminOrSuperAdmin(Long userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null && ("ADMIN".equals(role.getRoleCode()) || "SUPER_ADMIN".equals(role.getRoleCode()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreditorInfo updateCreditor(Long creditorId, CreditorUpdateRequest request) {
        CreditorInfo creditorInfo = getCreditorById(creditorId);

        if (request.getCreditorName() != null) {
            creditorInfo.setCreditorName(request.getCreditorName());
        }
        if (request.getCreditorType() != null) {
            creditorInfo.setCreditorType(request.getCreditorType());
        }
        if (request.getContactPhone() != null) {
            creditorInfo.setContactPhone(request.getContactPhone());
        }
        if (request.getContactEmail() != null) {
            creditorInfo.setContactEmail(request.getContactEmail());
        }
        if (request.getAddress() != null) {
            creditorInfo.setAddress(request.getAddress());
        }
        if (request.getIdNumber() != null) {
            creditorInfo.setIdNumber(request.getIdNumber());
        }
        if (request.getLegalRepresentative() != null) {
            creditorInfo.setLegalRepresentative(request.getLegalRepresentative());
        }
        if (request.getRegisteredCapital() != null) {
            creditorInfo.setRegisteredCapital(request.getRegisteredCapital());
        }
        if (request.getStatus() != null) {
            creditorInfo.setCreditorStatus(request.getStatus());
        }

        return creditorInfoRepository.save(creditorInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCreditor(Long creditorId) {
        if (!creditorInfoRepository.existsById(creditorId)) {
            throw new BusinessException("债权人不存在");
        }
        creditorInfoRepository.deleteById(creditorId);
    }

    @Override
    public PageResult<CreditorInfoResponse> getCreditorListWithCaseInfo(Integer pageNum, Integer pageSize, Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<CreditorInfo> spec = buildSpecificationWithPermission(caseId, creditorType, creditorName, idNumber, legalRepresentative, userId);
        Page<CreditorInfo> page = creditorInfoRepository.findAll(spec, pageable);
        
        List<CreditorInfo> creditorList = page.getContent();
        Long total = page.getTotalElements();

        List<Long> caseIds = creditorList.stream()
                .map(CreditorInfo::getCaseId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, BankruptCase> caseMap = caseIds.isEmpty() ? Map.of() : 
            caseIds.stream()
                .collect(Collectors.toMap(
                    id -> id,
                    id -> bankruptCaseService.getCaseById(id),
                    (existing, replacement) -> existing
                ));

        List<CreditorInfoResponse> responseList = creditorList.stream()
                .map(creditor -> {
                    CreditorInfoResponse response = new CreditorInfoResponse();
                    BeanUtils.copyProperties(creditor, response);
                    
                    if (creditor.getCaseId() != null) {
                        BankruptCase bankruptCase = caseMap.get(creditor.getCaseId());
                        if (bankruptCase != null) {
                            response.setCaseNumber(bankruptCase.getCaseNumber());
                            response.setCaseName(bankruptCase.getCaseName());
                        }
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());

        return PageResult.of(total, responseList);
    }
}
