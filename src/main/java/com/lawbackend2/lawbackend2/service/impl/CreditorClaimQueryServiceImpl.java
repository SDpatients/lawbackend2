package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.CreditorClaimQueryRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimQueryResponse;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.entity.ClaimReview;
import com.lawbackend2.lawbackend2.entity.CreditorInfo;
import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.ClaimConfirmationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimRegistrationRepository;
import com.lawbackend2.lawbackend2.repository.ClaimReviewRepository;
import com.lawbackend2.lawbackend2.repository.CreditorInfoRepository;
import com.lawbackend2.lawbackend2.service.CreditorClaimQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreditorClaimQueryServiceImpl implements CreditorClaimQueryService {

    private final ClaimRegistrationRepository claimRegistrationRepository;
    private final ClaimConfirmationRepository claimConfirmationRepository;
    private final ClaimReviewRepository claimReviewRepository;
    private final CreditorInfoRepository creditorInfoRepository;
    private final BankruptCaseRepository bankruptCaseRepository;

    public CreditorClaimQueryServiceImpl(ClaimRegistrationRepository claimRegistrationRepository,
                                         ClaimConfirmationRepository claimConfirmationRepository,
                                         ClaimReviewRepository claimReviewRepository,
                                         CreditorInfoRepository creditorInfoRepository,
                                         BankruptCaseRepository bankruptCaseRepository) {
        this.claimRegistrationRepository = claimRegistrationRepository;
        this.claimConfirmationRepository = claimConfirmationRepository;
        this.claimReviewRepository = claimReviewRepository;
        this.creditorInfoRepository = creditorInfoRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
    }

    @Override
    public PageResult<CreditorClaimQueryResponse> queryClaims(CreditorClaimQueryRequest request) {
        if (request == null || request.getCaseId() == null) {
            throw new IllegalArgumentException("案件 ID 不能为空");
        }

        Integer pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        List<CreditorInfo> creditors = creditorInfoRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("caseId"), request.getCaseId()));
            predicates.add(cb.equal(root.get("isDeleted"), false));
            
            if (request.getCreditorName() != null && !request.getCreditorName().isEmpty()) {
                predicates.add(cb.like(root.get("creditorName"), "%" + request.getCreditorName() + "%"));
            }
            if (request.getCreditorType() != null && !request.getCreditorType().isEmpty()) {
                predicates.add(cb.equal(root.get("creditorType"), request.getCreditorType()));
            }
            if (request.getCreditorStatus() != null && !request.getCreditorStatus().isEmpty()) {
                try {
                    CreditorStatus creditorStatus = CreditorStatus.valueOf(request.getCreditorStatus());
                    predicates.add(cb.equal(root.get("creditorStatus"), creditorStatus));
                } catch (IllegalArgumentException e) {
                    log.warn("无效的债权人状态：{}", request.getCreditorStatus());
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        List<Long> creditorIds = creditors.stream()
                .map(CreditorInfo::getId)
                .collect(Collectors.toList());

        Map<Long, BankruptCase> caseMap = creditors.stream()
                .map(CreditorInfo::getCaseId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> bankruptCaseRepository.findById(id).orElse(null),
                        (existing, replacement) -> existing
                ));

        List<ClaimRegistration> registrations = claimRegistrationRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("isDeleted")));
            predicates.add(cb.equal(root.get("caseId"), request.getCaseId()));
            
            if (request.getClaimType() != null && !request.getClaimType().isEmpty()) {
                predicates.add(cb.equal(root.get("claimType"), request.getClaimType()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        });

        Map<String, List<ClaimRegistration>> registrationMap = registrations.stream()
                .collect(Collectors.groupingBy(ClaimRegistration::getCreditorName));

        Map<Long, List<ClaimConfirmation>> confirmationMap = claimConfirmationRepository.findAll()
                .stream()
                .filter(cc -> cc.getClaimRegistrationId() != null && !cc.getIsDeleted())
                .collect(Collectors.groupingBy(ClaimConfirmation::getClaimRegistrationId));

        Map<Long, List<ClaimReview>> reviewMap = claimReviewRepository.findAll()
                .stream()
                .filter(cr -> cr.getClaimRegistrationId() != null && !cr.getIsDeleted())
                .collect(Collectors.groupingBy(ClaimReview::getClaimRegistrationId));

        List<CreditorClaimQueryResponse> list = creditors.stream()
                .map(creditor -> {
                    CreditorClaimQueryResponse response = new CreditorClaimQueryResponse();
                    
                    response.setCreditorId(creditor.getId());
                    response.setCaseId(creditor.getCaseId());
                    response.setCreditorName(creditor.getCreditorName());
                    response.setCreditorType(creditor.getCreditorType());
                    response.setCreditorStatus(creditor.getCreditorStatus() != null ? creditor.getCreditorStatus().name() : null);
                    response.setContactPhone(creditor.getContactPhone());
                    response.setContactEmail(creditor.getContactEmail());
                    response.setAddress(creditor.getAddress());
                    response.setIdNumber(creditor.getIdNumber());
                    response.setLegalRepresentative(creditor.getLegalRepresentative());
                    response.setRegisteredCapital(creditor.getRegisteredCapital());
                    response.setCreateTime(creditor.getCreateTime());
                    response.setUpdateTime(creditor.getUpdateTime());

                    if (creditor.getCaseId() != null) {
                        BankruptCase bankruptCase = caseMap.get(creditor.getCaseId());
                        if (bankruptCase != null) {
                            response.setCaseNumber(bankruptCase.getCaseNumber());
                            response.setCaseName(bankruptCase.getCaseName());
                        }
                    }
                    
                    List<ClaimRegistration> creditorRegistrations = registrationMap.getOrDefault(creditor.getCreditorName(), new ArrayList<>());
                    ClaimRegistration latestRegistration = creditorRegistrations.stream()
                            .max((a, b) -> {
                                if (a.getRegistrationDate() == null) return -1;
                                if (b.getRegistrationDate() == null) return 1;
                                return a.getRegistrationDate().compareTo(b.getRegistrationDate());
                            })
                            .orElse(null);
                    
                    if (latestRegistration != null) {
                        response.setClaimType(latestRegistration.getClaimType());
                        response.setAccountName(latestRegistration.getAccountName());
                        response.setCreditorBankAccount(latestRegistration.getCreditorBankAccount());
                        response.setBankName(latestRegistration.getBankName());
                        
                        response.setDeclaredPrincipal(latestRegistration.getPrincipal());
                        response.setDeclaredInterest(latestRegistration.getInterest());
                        response.setDeclaredPenalty(latestRegistration.getPenalty());
                        response.setDeclaredOtherLosses(latestRegistration.getOtherLosses());
                        response.setDeclaredTotalAmount(latestRegistration.getTotalAmount());
                        
                        response.setRemarks(latestRegistration.getRemarks());
                        
                        List<ClaimConfirmation> confirmations = confirmationMap.getOrDefault(latestRegistration.getId(), new ArrayList<>());
                        ClaimConfirmation latestConfirmation = confirmations.stream()
                                .max((a, b) -> {
                                    if (a.getFinalConfirmationDate() == null) return -1;
                                    if (b.getFinalConfirmationDate() == null) return 1;
                                    return a.getFinalConfirmationDate().compareTo(b.getFinalConfirmationDate());
                                })
                                .orElse(null);
                        
                        if (latestConfirmation != null) {
                            response.setConfirmedPrincipal(latestConfirmation.getConfirmedPrincipal());
                            response.setConfirmedInterest(latestConfirmation.getConfirmedInterest());
                            response.setConfirmedPenalty(latestConfirmation.getConfirmedPenalty());
                            response.setConfirmedOtherLosses(latestConfirmation.getConfirmedOtherLosses());
                            response.setConfirmedTotalAmount(latestConfirmation.getConfirmedTotalAmount());
                            
                            BigDecimal confirmedTotal = latestConfirmation.getConfirmedTotalAmount() != null 
                                    ? latestConfirmation.getConfirmedTotalAmount() 
                                    : BigDecimal.ZERO;
                            BigDecimal declaredTotal = latestRegistration.getTotalAmount() != null 
                                    ? latestRegistration.getTotalAmount() 
                                    : BigDecimal.ZERO;
                            BigDecimal reduction = declaredTotal.subtract(confirmedTotal);
                            response.setReductionAmount(reduction.compareTo(BigDecimal.ZERO) > 0 ? reduction : BigDecimal.ZERO);
                        } else {
                            List<ClaimReview> reviews = reviewMap.getOrDefault(latestRegistration.getId(), new ArrayList<>());
                            ClaimReview latestReview = reviews.stream()
                                    .max((a, b) -> {
                                        if (a.getReviewDate() == null) return -1;
                                        if (b.getReviewDate() == null) return 1;
                                        return a.getReviewDate().compareTo(b.getReviewDate());
                                    })
                                    .orElse(null);
                            
                            if (latestReview != null) {
                                response.setConfirmedPrincipal(latestReview.getConfirmedPrincipal());
                                response.setConfirmedInterest(latestReview.getConfirmedInterest());
                                response.setConfirmedPenalty(latestReview.getConfirmedPenalty());
                                response.setConfirmedOtherLosses(latestReview.getConfirmedOtherLosses());
                                response.setConfirmedTotalAmount(latestReview.getConfirmedTotalAmount());
                                
                                BigDecimal confirmedTotal = latestReview.getConfirmedTotalAmount() != null 
                                        ? latestReview.getConfirmedTotalAmount() 
                                        : BigDecimal.ZERO;
                                BigDecimal declaredTotal = latestRegistration.getTotalAmount() != null 
                                        ? latestRegistration.getTotalAmount() 
                                        : BigDecimal.ZERO;
                                BigDecimal reduction = declaredTotal.subtract(confirmedTotal);
                                response.setReductionAmount(reduction.compareTo(BigDecimal.ZERO) > 0 ? reduction : BigDecimal.ZERO);
                            }
                        }
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());

        Long total = (long) list.size();
        
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        
        if (fromIndex >= list.size()) {
            return PageResult.of(total, new ArrayList<>(), pageNum, pageSize);
        }
        
        List<CreditorClaimQueryResponse> pagedList = list.subList(fromIndex, toIndex);

        return PageResult.of(total, pagedList, pageNum, pageSize);
    }

    private String getRegistrationStatusDesc(String status) {
        if (status == null) {
            return "待登记";
        }
        switch (status) {
            case "PENDING":
                return "待登记";
            case "REVIEW_COMPLETED":
                return "审查完成";
            case "CONFIRMING":
                return "确认中";
            case "CONFIRMED":
                return "已确认";
            case "REGISTERED":
                return "已登记";
            case "REJECTED":
                return "已驳回";
            default:
                return status;
        }
    }
}
