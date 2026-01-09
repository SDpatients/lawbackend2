package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.DebtorCreateRequest;
import com.lawbackend2.lawbackend2.dto.DebtorEnterpriseResponse;
import com.lawbackend2.lawbackend2.dto.DebtorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.DebtorEnterprise;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.DebtorEnterpriseRepository;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
import com.lawbackend2.lawbackend2.service.DebtorEnterpriseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DebtorEnterpriseServiceImpl implements DebtorEnterpriseService {

    private final DebtorEnterpriseRepository debtorEnterpriseRepository;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final BankruptCaseService bankruptCaseService;

    public DebtorEnterpriseServiceImpl(DebtorEnterpriseRepository debtorEnterpriseRepository,
                                     BankruptCaseRepository bankruptCaseRepository,
                                     BankruptCaseService bankruptCaseService) {
        this.debtorEnterpriseRepository = debtorEnterpriseRepository;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.bankruptCaseService = bankruptCaseService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DebtorEnterprise createDebtor(DebtorCreateRequest request, Long userId) {
        log.info("创建债务人信息, 统一社会信用代码: {}, 创建人ID: {}", request.getUnifiedSocialCreditCode(), userId);

        BankruptCase bankruptCase = bankruptCaseRepository.findById(request.getCaseId())
                .orElseThrow(() -> new BusinessException("案件不存在"));

        DebtorEnterprise debtorEnterprise = new DebtorEnterprise();
        BeanUtils.copyProperties(request, debtorEnterprise);
        debtorEnterprise.setCreateUserId(userId);
        debtorEnterprise.setUpdateUserId(userId);

        DebtorEnterprise saved = debtorEnterpriseRepository.save(debtorEnterprise);
        log.info("债务人信息创建成功, ID: {}", saved.getId());
        return saved;
    }

    @Override
    public DebtorEnterprise getDebtorById(Long debtorId) {
        log.debug("查询债务人信息, ID: {}", debtorId);
        return debtorEnterpriseRepository.findById(debtorId)
                .orElseThrow(() -> new BusinessException("债务人信息不存在"));
    }

    @Override
    public DebtorEnterpriseResponse getDebtorByIdWithCaseInfo(Long debtorId) {
        DebtorEnterprise debtorEnterprise = getDebtorById(debtorId);
        
        DebtorEnterpriseResponse response = new DebtorEnterpriseResponse();
        BeanUtils.copyProperties(debtorEnterprise, response);
        
        if (debtorEnterprise.getCaseId() != null) {
            BankruptCase bankruptCase = bankruptCaseService.getCaseById(debtorEnterprise.getCaseId());
            if (bankruptCase != null) {
                response.setCaseNumber(bankruptCase.getCaseNumber());
                response.setCaseName(bankruptCase.getCaseName());
            }
        }
        
        return response;
    }

    @Override
    public List<DebtorEnterprise> getDebtorList(Integer pageNum, Integer pageSize, Long caseId, String enterpriseName) {
        log.debug("查询债务人列表, pageNum: {}, pageSize: {}, caseId: {}, enterpriseName: {}", 
                  pageNum, pageSize, caseId, enterpriseName);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<DebtorEnterprise> page;
        if (caseId != null && enterpriseName != null && !enterpriseName.isEmpty()) {
            page = debtorEnterpriseRepository.findByConditions(caseId, enterpriseName, null, null, pageable);
        } else if (caseId != null) {
            page = debtorEnterpriseRepository.findByCaseId(caseId, pageable);
        } else {
            page = debtorEnterpriseRepository.findAll(pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getDebtorCount(Long caseId, String enterpriseName) {
        Pageable pageable = Pageable.unpaged();

        if (caseId != null && enterpriseName != null && !enterpriseName.isEmpty()) {
            return debtorEnterpriseRepository.findByConditions(caseId, enterpriseName, null, null, pageable).getTotalElements();
        } else if (caseId != null) {
            return debtorEnterpriseRepository.findByCaseId(caseId, pageable).getTotalElements();
        } else {
            return debtorEnterpriseRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DebtorEnterprise updateDebtor(Long debtorId, DebtorUpdateRequest request) {
        log.info("更新债务人信息, ID: {}", debtorId);

        DebtorEnterprise debtorEnterprise = getDebtorById(debtorId);

        if (request.getEnterpriseName() != null) {
            debtorEnterprise.setEnterpriseName(request.getEnterpriseName());
        }
        if (request.getLegalRepresentative() != null) {
            debtorEnterprise.setLegalRepresentative(request.getLegalRepresentative());
        }
        if (request.getContactPhone() != null) {
            debtorEnterprise.setContactPhone(request.getContactPhone());
        }
        if (request.getContactPerson() != null) {
            debtorEnterprise.setContactPerson(request.getContactPerson());
        }
        if (request.getBusinessScope() != null) {
            debtorEnterprise.setBusinessScope(request.getBusinessScope());
        }
        if (request.getIndustry() != null) {
            debtorEnterprise.setIndustry(request.getIndustry());
        }
        if (request.getRegisteredAddress() != null) {
            debtorEnterprise.setRegisteredAddress(request.getRegisteredAddress());
        }

        DebtorEnterprise updated = debtorEnterpriseRepository.save(debtorEnterprise);
        log.info("债务人信息更新成功, ID: {}", updated.getId());
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDebtor(Long debtorId) {
        log.info("删除债务人信息, ID: {}", debtorId);
        if (!debtorEnterpriseRepository.existsById(debtorId)) {
            throw new BusinessException("债务人信息不存在");
        }
        debtorEnterpriseRepository.deleteById(debtorId);
        log.info("债务人信息删除成功, ID: {}", debtorId);
    }

    @Override
    public PageResult<DebtorEnterpriseResponse> getDebtorListWithCaseInfo(Integer pageNum, Integer pageSize, Long caseId, String enterpriseName, String unifiedSocialCreditCode, String legalRepresentative) {
        log.debug("查询债务人列表（含案件信息）, pageNum: {}, pageSize: {}, caseId: {}, enterpriseName: {}, unifiedSocialCreditCode: {}, legalRepresentative: {}", 
                  pageNum, pageSize, caseId, enterpriseName, unifiedSocialCreditCode, legalRepresentative);

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<DebtorEnterprise> page = debtorEnterpriseRepository.findByConditions(caseId, enterpriseName, unifiedSocialCreditCode, legalRepresentative, pageable);
        List<DebtorEnterprise> debtorList = page.getContent();
        Long total = page.getTotalElements();

        List<Long> caseIds = debtorList.stream()
                .map(DebtorEnterprise::getCaseId)
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

        List<DebtorEnterpriseResponse> responseList = debtorList.stream()
                .map(debtor -> {
                    DebtorEnterpriseResponse response = new DebtorEnterpriseResponse();
                    BeanUtils.copyProperties(debtor, response);
                    
                    if (debtor.getCaseId() != null) {
                        BankruptCase bankruptCase = caseMap.get(debtor.getCaseId());
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
