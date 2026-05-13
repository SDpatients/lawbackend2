package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStagesResponse;
import com.lawbackend2.lawbackend2.dto.CreditorCreateRequest;
import com.lawbackend2.lawbackend2.dto.CreditorInfoResponse;
import com.lawbackend2.lawbackend2.dto.CreditorSimpleResponse;
import com.lawbackend2.lawbackend2.dto.CreditorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import com.lawbackend2.lawbackend2.entity.Role;
import com.lawbackend2.lawbackend2.entity.WorkTeamMember;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import com.lawbackend2.lawbackend2.repository.RoleRepository;
import com.lawbackend2.lawbackend2.repository.UserRoleRepository;
import com.lawbackend2.lawbackend2.repository.WorkTeamMemberRepository;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimReviewRepository;
import com.lawbackend2.lawbackend2.repository.ClaimConfirmationRepository;
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

import javax.persistence.criteria.Join;
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
    private final ClaimRegistrationRepository claimRegistrationRepository;
    private final ClaimReviewRepository claimReviewRepository;
    private final ClaimConfirmationRepository claimConfirmationRepository;

    public CreditorInfoServiceImpl(CreditorInfoRepository creditorInfoRepository, BankruptCaseService bankruptCaseService, WorkTeamMemberRepository workTeamMemberRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, ClaimRegistrationRepository claimRegistrationRepository, ClaimReviewRepository claimReviewRepository, ClaimConfirmationRepository claimConfirmationRepository) {
        this.creditorInfoRepository = creditorInfoRepository;
        this.bankruptCaseService = bankruptCaseService;
        this.workTeamMemberRepository = workTeamMemberRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.claimReviewRepository = claimReviewRepository;
        this.claimConfirmationRepository = claimConfirmationRepository;
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
        response.setCreditorStatus(creditorInfo.getCreditorStatus());
        if (creditorInfo.getCreditorStatus() != null) {
            response.setStatus(creditorInfo.getCreditorStatus().name());
        }
        
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
    public List<CreditorInfo> getCreditorList(Integer pageNum, Integer pageSize, Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<CreditorInfo> spec = buildSpecification(caseId, creditorType, creditorName, idNumber, legalRepresentative, status);
        Page<CreditorInfo> page = creditorInfoRepository.findAll(spec, pageable);

        return page.getContent();
    }

    @Override
    public Long getCreditorCount(Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status) {
        Specification<CreditorInfo> spec = buildSpecification(caseId, creditorType, creditorName, idNumber, legalRepresentative, status);
        return creditorInfoRepository.count(spec);
    }

    private Specification<CreditorInfo> buildSpecification(Long caseId, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status) {
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
                predicates.add(cb.equal(root.get("idNumber"), idNumber));
            }

            if (legalRepresentative != null && !legalRepresentative.trim().isEmpty()) {
                predicates.add(cb.like(root.get("legalRepresentative"), "%" + legalRepresentative + "%"));
            }

            if (status != null && !status.trim().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("creditorStatus"), CreditorStatus.valueOf(status)));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status values
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<CreditorInfo> buildSpecificationWithPermission(Long caseId, String caseNumber, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status, Long userId) {
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
                predicates.add(cb.equal(root.get("idNumber"), idNumber));
            }

            if (legalRepresentative != null && !legalRepresentative.trim().isEmpty()) {
                predicates.add(cb.like(root.get("legalRepresentative"), "%" + legalRepresentative + "%"));
            }

            if (status != null && !status.trim().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("creditorStatus"), CreditorStatus.valueOf(status)));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status values
                }
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

    private Specification<CreditorInfo> buildSpecificationWithCaseNumber(Long caseId, String caseNumber, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (caseId != null) {
                predicates.add(cb.equal(root.get("caseId"), caseId));
            }

            if (caseNumber != null && !caseNumber.trim().isEmpty()) {
                Join<CreditorInfo, BankruptCase> caseJoin = root.join("bankruptCase", javax.persistence.criteria.JoinType.INNER);
                predicates.add(cb.like(caseJoin.get("caseNumber"), "%" + caseNumber + "%"));
            }

            if (creditorType != null && !creditorType.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("creditorType"), creditorType));
            }

            if (creditorName != null && !creditorName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("creditorName"), "%" + creditorName + "%"));
            }

            if (idNumber != null && !idNumber.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("idNumber"), idNumber));
            }

            if (legalRepresentative != null && !legalRepresentative.trim().isEmpty()) {
                predicates.add(cb.like(root.get("legalRepresentative"), "%" + legalRepresentative + "%"));
            }

            if (status != null && !status.trim().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("creditorStatus"), CreditorStatus.valueOf(status)));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid status values
                }
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
        if (request.getCreditorStatus() != null) {
            creditorInfo.setCreditorStatus(request.getCreditorStatus());
        }

        return creditorInfoRepository.save(creditorInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCreditor(Long creditorId, Long userId) {
        CreditorInfo creditorInfo = getCreditorById(creditorId);
        
        // 检查权限
        checkPermission(creditorInfo, userId);
        
        // 级联删除：债权确认 → 债权审查 → 债权申报
        // 1. 查找该债权人的所有债权申报
        List<ClaimRegistration> registrations = claimRegistrationRepository.findAllByCreditorName(creditorInfo.getCreditorName());
        for (ClaimRegistration registration : registrations) {
            // 删除相关的确认记录
            List<ClaimConfirmation> confirmations = claimConfirmationRepository.findByClaimRegistrationId(registration.getId());
            for (ClaimConfirmation confirmation : confirmations) {
                claimConfirmationRepository.delete(confirmation);
                log.info("级联删除债权确认记录, confirmationId: {}, claimRegistrationId: {}", confirmation.getId(), registration.getId());
            }
            
            // 删除相关的审查记录
            List<ClaimReview> reviews = claimReviewRepository.findAllByClaimRegistrationId(registration.getId());
            for (ClaimReview review : reviews) {
                claimReviewRepository.delete(review);
                log.info("级联删除债权审查记录, reviewId: {}, claimRegistrationId: {}", review.getId(), registration.getId());
            }
            
            // 删除债权申报
            claimRegistrationRepository.delete(registration);
            log.info("级联删除债权申报记录, claimId: {}, creditorName: {}", registration.getId(), creditorInfo.getCreditorName());
        }
        
        // 硬删除债权人
        creditorInfoRepository.delete(creditorInfo);
        
        log.info("债权人删除成功, creditorId: {}, creditorName: {}", creditorId, creditorInfo.getCreditorName());
    }

    @Override
    public PageResult<CreditorInfoResponse> getCreditorListWithCaseInfo(Integer pageNum, Integer pageSize, Long caseId, String caseNumber, String creditorType, String creditorName, String idNumber, String legalRepresentative, String status, Long userId) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<CreditorInfo> spec = buildSpecificationWithCaseNumber(caseId, caseNumber, creditorType, creditorName, idNumber, legalRepresentative, status, userId);
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
                    response.setCreditorStatus(creditor.getCreditorStatus());
                    if (creditor.getCreditorStatus() != null) {
                        response.setStatus(creditor.getCreditorStatus().name());
                    }
                    
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

    @Override
    public CreditorClaimStagesResponse getCreditorClaimStages(Long creditorId, Long userId) {
        CreditorInfo creditorInfo = getCreditorById(creditorId);
        
        checkPermission(creditorInfo, userId);
        
        CreditorClaimStagesResponse response = new CreditorClaimStagesResponse();
        response.setCreditorId(creditorInfo.getId());
        response.setCreditorName(creditorInfo.getCreditorName());
        
        List<CreditorClaimStagesResponse.ClaimRegistrationInfo> claimRegistrations = claimRegistrationRepository
                .findAllByCreditorName(creditorInfo.getCreditorName())
                .stream()
                .map(reg -> {
                    CreditorClaimStagesResponse.ClaimRegistrationInfo info = new CreditorClaimStagesResponse.ClaimRegistrationInfo();
                    BeanUtils.copyProperties(reg, info);
                    return info;
                })
                .collect(Collectors.toList());
        response.setClaimRegistrations(claimRegistrations);

        List<CreditorClaimStagesResponse.ClaimReviewInfo> claimReviews = claimReviewRepository
                .findAllByCreditorName(creditorInfo.getCreditorName())
                .stream()
                .map(review -> {
                    CreditorClaimStagesResponse.ClaimReviewInfo info = new CreditorClaimStagesResponse.ClaimReviewInfo();
                    BeanUtils.copyProperties(review, info);
                    return info;
                })
                .collect(Collectors.toList());
        response.setClaimReviews(claimReviews);

        List<CreditorClaimStagesResponse.ClaimConfirmationInfo> claimConfirmations = claimConfirmationRepository
                .findAllByCreditorName(creditorInfo.getCreditorName())
                .stream()
                .map(confirmation -> {
                    CreditorClaimStagesResponse.ClaimConfirmationInfo info = new CreditorClaimStagesResponse.ClaimConfirmationInfo();
                    BeanUtils.copyProperties(confirmation, info);
                    return info;
                })
                .collect(Collectors.toList());
        response.setClaimConfirmations(claimConfirmations);
        
        return response;
    }

    @Override
    public List<CreditorSimpleResponse> searchCreditorsByName(Long caseId, String creditorName, Integer limit, Long userId) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createTime"));

        Specification<CreditorInfo> spec = buildSearchSpecification(caseId, creditorName, userId);
        Page<CreditorInfo> page = creditorInfoRepository.findAll(spec, pageable);
        
        List<CreditorInfo> creditorList = page.getContent();

        return creditorList.stream()
                .map(creditor -> {
                    CreditorSimpleResponse response = new CreditorSimpleResponse();
                    response.setId(creditor.getId());
                    response.setCaseId(creditor.getCaseId());
                    response.setCreditorName(creditor.getCreditorName());
                    response.setIdNumber(creditor.getIdNumber());
                    response.setCreditorType(creditor.getCreditorType());
                    response.setLegalRepresentative(creditor.getLegalRepresentative());
                    response.setAddress(creditor.getAddress());
                    return response;
                })
                .collect(Collectors.toList());
    }

    private Specification<CreditorInfo> buildSearchSpecification(Long caseId, String creditorName, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (caseId != null) {
                predicates.add(cb.equal(root.get("caseId"), caseId));
            }

            if (creditorName != null && !creditorName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("creditorName"), "%" + creditorName + "%"));
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
}
