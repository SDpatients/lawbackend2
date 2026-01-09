package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.*;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
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
public class BankruptCaseServiceImpl implements BankruptCaseService {

    private final BankruptCaseRepository bankruptCaseRepository;

    public BankruptCaseServiceImpl(BankruptCaseRepository bankruptCaseRepository) {
        this.bankruptCaseRepository = bankruptCaseRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankruptCase createCase(CaseCreateRequest request, Long userId) {
        if (bankruptCaseRepository.findByCaseNumber(request.getCaseNumber()).isPresent()) {
            throw new BusinessException("案号已存在");
        }

        BankruptCase bankruptCase = new BankruptCase();
        BeanUtils.copyProperties(request, bankruptCase);
        bankruptCase.setCreatorId(userId);
        bankruptCase.setCreateUserId(userId);
        bankruptCase.setUpdateUserId(userId);
        bankruptCase.setIsSimplifiedTrial(request.getIsSimplifiedTrial() != null && request.getIsSimplifiedTrial() == 1);

        return bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    public BankruptCase getCaseById(Long caseId) {
        return bankruptCaseRepository.findById(caseId)
                .orElseThrow(() -> new BusinessException("案件不存在"));
    }

    @Override
    public List<BankruptCase> getCaseList(Integer pageNum, Integer pageSize, String caseStatus, String caseProgress) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<BankruptCase> page;
        if (caseStatus != null && caseProgress != null) {
            page = bankruptCaseRepository.findByCaseStatusAndCaseProgress(caseStatus, caseProgress, pageable);
        } else if (caseStatus != null) {
            page = bankruptCaseRepository.findByCaseStatus(caseStatus, pageable);
        } else if (caseProgress != null) {
            page = bankruptCaseRepository.findByCaseProgress(caseProgress, pageable);
        } else {
            page = bankruptCaseRepository.findAll(pageable);
        }

        return page.getContent();
    }

    @Override
    public Long getCaseCount(String caseStatus, String caseProgress) {
        if (caseStatus != null && caseProgress != null) {
            return bankruptCaseRepository.findByCaseStatusAndCaseProgress(caseStatus, caseProgress, Pageable.unpaged()).getTotalElements();
        } else if (caseStatus != null) {
            return bankruptCaseRepository.findByCaseStatus(caseStatus, Pageable.unpaged()).getTotalElements();
        } else if (caseProgress != null) {
            return bankruptCaseRepository.findByCaseProgress(caseProgress, Pageable.unpaged()).getTotalElements();
        } else {
            return bankruptCaseRepository.count();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankruptCase updateCase(Long caseId, CaseUpdateRequest request) {
        BankruptCase bankruptCase = getCaseById(caseId);

        if (request.getCaseName() != null) {
            bankruptCase.setCaseName(request.getCaseName());
        }
        if (request.getCaseReason() != null) {
            bankruptCase.setCaseReason(request.getCaseReason());
        }
        if (request.getRemarks() != null) {
            bankruptCase.setRemarks(request.getRemarks());
        }
        if (request.getFilingDate() != null) {
            bankruptCase.setFilingDate(request.getFilingDate());
        }
        if (request.getCaseProgress() != null) {
            bankruptCase.setCaseProgress(request.getCaseProgress());
        }
        if (request.getMainResponsiblePerson() != null) {
            bankruptCase.setMainResponsiblePerson(request.getMainResponsiblePerson());
        }
        if (request.getDesignatedInstitution() != null) {
            bankruptCase.setDesignatedInstitution(request.getDesignatedInstitution());
        }
        if (request.getAcceptanceCourt() != null) {
            bankruptCase.setAcceptanceCourt(request.getAcceptanceCourt());
        }
        if (request.getDebtClaimDeadline() != null) {
            bankruptCase.setDebtClaimDeadline(request.getDebtClaimDeadline());
        }

        return bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCaseStatus(Long caseId, CaseStatusUpdateRequest request) {
        BankruptCase bankruptCase = getCaseById(caseId);
        bankruptCase.setCaseStatus(request.getCaseStatus());
        bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCaseProgress(Long caseId, CaseProgressUpdateRequest request) {
        BankruptCase bankruptCase = getCaseById(caseId);
        bankruptCase.setCaseProgress(request.getProgressStatus());
        bankruptCaseRepository.save(bankruptCase);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewCase(Long caseId, CaseReviewRequest request, Long userId) {
        log.info("案件审核, caseId: {}, reviewStatus: {}, reviewerId: {}", 
                  caseId, request.getReviewStatus(), userId);

        BankruptCase bankruptCase = getCaseById(caseId);

        if (!"PENDING".equals(bankruptCase.getReviewStatus())) {
            throw new BusinessException("案件当前状态不允许审核");
        }

        bankruptCase.setReviewStatus(request.getReviewStatus());
        bankruptCase.setReviewerId(userId);
        bankruptCase.setReviewOpinion(request.getReviewOpinion());
        bankruptCase.setReviewTime(java.time.LocalDateTime.now());
        bankruptCase.setReviewCount(bankruptCase.getReviewCount() + 1);

        if ("APPROVED".equals(request.getReviewStatus())) {
            bankruptCase.setCaseStatus("IN_PROGRESS");
            log.info("案件审核通过, caseId: {}", caseId);
        } else if ("REJECTED".equals(request.getReviewStatus())) {
            bankruptCase.setCaseStatus("PENDING");
            log.info("案件审核驳回, caseId: {}", caseId);
        }

        bankruptCaseRepository.save(bankruptCase);
        log.info("案件审核完成, caseId: {}, reviewStatus: {}", caseId, request.getReviewStatus());
    }

    @Override
    public BankruptCase getReviewStatus(Long caseId) {
        log.debug("查询案件审核状态, caseId: {}", caseId);
        BankruptCase bankruptCase = getCaseById(caseId);
        return bankruptCase;
    }

    @Override
    public List<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> getCaseSimpleList(Integer page, Integer size, String caseNumber) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> result = bankruptCaseRepository.findSimpleInfoByCaseNumber(caseNumber, pageable);
        return result.getContent();
    }

    @Override
    public Long getCaseSimpleCount(String caseNumber) {
        Page<com.lawbackend2.lawbackend2.dto.CaseSimpleInfo> result = bankruptCaseRepository.findSimpleInfoByCaseNumber(caseNumber, Pageable.unpaged());
        return result.getTotalElements();
    }
}
