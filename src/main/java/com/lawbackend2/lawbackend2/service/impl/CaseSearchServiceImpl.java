package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.CaseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
public class CaseSearchServiceImpl implements CaseSearchService {

    @Autowired
    private BankruptCaseRepository caseRepository;

    @Override
    public List<BankruptCase> searchByKeyword(Integer page, Integer size, String keyword) {
        log.info("搜索案件，关键词：{}，page：{}，size：{}", keyword, page, size);

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new BusinessException("搜索关键词不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByKeyword(keyword.trim(), pageable);

            log.info("搜索案件成功，关键词：{}，结果数量：{}", keyword, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("搜索案件失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("搜索案件失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("搜索案件失败");
        }
    }

    @Override
    public List<BankruptCase> searchByKeywordAndStatus(Integer page, Integer size, String keyword, String caseStatus) {
        log.info("搜索案件，关键词：{}，案件状态：{}，page：{}，size：{}", keyword, caseStatus, page, size);

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new BusinessException("搜索关键词不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByKeywordAndCaseStatus(keyword.trim(), caseStatus, pageable);

            log.info("搜索案件成功，关键词：{}，案件状态：{}，结果数量：{}", keyword, caseStatus, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("搜索案件失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("搜索案件失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("搜索案件失败");
        }
    }

    @Override
    public List<BankruptCase> searchByKeywordAndProgress(Integer page, Integer size, String keyword, String caseProgress) {
        log.info("搜索案件，关键词：{}，案件进度：{}，page：{}，size：{}", keyword, caseProgress, page, size);

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new BusinessException("搜索关键词不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByKeywordAndCaseProgress(keyword.trim(), caseProgress, pageable);

            log.info("搜索案件成功，关键词：{}，案件进度：{}，结果数量：{}", keyword, caseProgress, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("搜索案件失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("搜索案件失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("搜索案件失败");
        }
    }

    @Override
    public List<BankruptCase> searchByKeywordAndStatusAndProgress(Integer page, Integer size, String keyword, String caseStatus, String caseProgress) {
        log.info("搜索案件，关键词：{}，案件状态：{}，案件进度：{}，page：{}，size：{}", keyword, caseStatus, caseProgress, page, size);

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new BusinessException("搜索关键词不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByKeywordAndStatusAndProgress(keyword.trim(), caseStatus, caseProgress, pageable);

            log.info("搜索案件成功，关键词：{}，案件状态：{}，案件进度：{}，结果数量：{}", keyword, caseStatus, caseProgress, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("搜索案件失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("搜索案件失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("搜索案件失败");
        }
    }

    @Override
    public List<BankruptCase> searchByCaseNumber(Integer page, Integer size, String caseNumber) {
        log.info("按案件编号搜索，案件编号：{}，page：{}，size：{}", caseNumber, page, size);

        try {
            if (caseNumber == null || caseNumber.trim().isEmpty()) {
                throw new BusinessException("案件编号不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByCaseNumber(caseNumber.trim(), pageable);

            log.info("按案件编号搜索成功，案件编号：{}，结果数量：{}", caseNumber, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按案件编号搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按案件编号搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按案件编号搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByCaseName(Integer page, Integer size, String caseName) {
        log.info("按案件名称搜索，案件名称：{}，page：{}，size：{}", caseName, page, size);

        try {
            if (caseName == null || caseName.trim().isEmpty()) {
                throw new BusinessException("案件名称不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByCaseName(caseName.trim(), pageable);

            log.info("按案件名称搜索成功，案件名称：{}，结果数量：{}", caseName, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按案件名称搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按案件名称搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按案件名称搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByAcceptanceCourt(Integer page, Integer size, String acceptanceCourt) {
        log.info("按受理法院搜索，受理法院：{}，page：{}，size：{}", acceptanceCourt, page, size);

        try {
            if (acceptanceCourt == null || acceptanceCourt.trim().isEmpty()) {
                throw new BusinessException("受理法院不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByAcceptanceCourt(acceptanceCourt.trim(), pageable);

            log.info("按受理法院搜索成功，受理法院：{}，结果数量：{}", acceptanceCourt, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按受理法院搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按受理法院搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按受理法院搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByDesignatedInstitution(Integer page, Integer size, String designatedInstitution) {
        log.info("按指定机构搜索，指定机构：{}，page：{}，size：{}", designatedInstitution, page, size);

        try {
            if (designatedInstitution == null || designatedInstitution.trim().isEmpty()) {
                throw new BusinessException("指定机构不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByDesignatedInstitution(designatedInstitution.trim(), pageable);

            log.info("按指定机构搜索成功，指定机构：{}，结果数量：{}", designatedInstitution, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按指定机构搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按指定机构搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按指定机构搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByMainResponsiblePerson(Integer page, Integer size, String mainResponsiblePerson) {
        log.info("按主要负责人搜索，主要负责人：{}，page：{}，size：{}", mainResponsiblePerson, page, size);

        try {
            if (mainResponsiblePerson == null || mainResponsiblePerson.trim().isEmpty()) {
                throw new BusinessException("主要负责人不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByMainResponsiblePerson(mainResponsiblePerson.trim(), pageable);

            log.info("按主要负责人搜索成功，主要负责人：{}，结果数量：{}", mainResponsiblePerson, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按主要负责人搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按主要负责人搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按主要负责人搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByAcceptanceDateRange(Integer page, Integer size, LocalDate startDate, LocalDate endDate) {
        log.info("按受理日期范围搜索，开始日期：{}，结束日期：{}，page：{}，size：{}", startDate, endDate, page, size);

        try {
            if (startDate == null || endDate == null) {
                throw new BusinessException("开始日期和结束日期不能为空");
            }

            if (startDate.isAfter(endDate)) {
                throw new BusinessException("开始日期不能晚于结束日期");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByAcceptanceDateRange(startDate, endDate, pageable);

            log.info("按受理日期范围搜索成功，开始日期：{}，结束日期：{}，结果数量：{}", startDate, endDate, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按受理日期范围搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按受理日期范围搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按受理日期范围搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByCaseSource(Integer page, Integer size, String caseSource) {
        log.info("按案件来源搜索，案件来源：{}，page：{}，size：{}", caseSource, page, size);

        try {
            if (caseSource == null || caseSource.trim().isEmpty()) {
                throw new BusinessException("案件来源不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByCaseSource(caseSource.trim(), pageable);

            log.info("按案件来源搜索成功，案件来源：{}，结果数量：{}", caseSource, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按案件来源搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按案件来源搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按案件来源搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByCaseReason(Integer page, Integer size, String caseReason) {
        log.info("按案件原因搜索，案件原因：{}，page：{}，size：{}", caseReason, page, size);

        try {
            if (caseReason == null || caseReason.trim().isEmpty()) {
                throw new BusinessException("案件原因不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByCaseReason(caseReason.trim(), pageable);

            log.info("按案件原因搜索成功，案件原因：{}，结果数量：{}", caseReason, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按案件原因搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按案件原因搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按案件原因搜索失败");
        }
    }

    @Override
    public List<BankruptCase> searchByDesignatedJudge(Integer page, Integer size, String designatedJudge) {
        log.info("按指定法官搜索，指定法官：{}，page：{}，size：{}", designatedJudge, page, size);

        try {
            if (designatedJudge == null || designatedJudge.trim().isEmpty()) {
                throw new BusinessException("指定法官不能为空");
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BankruptCase> casePage = caseRepository.searchByDesignatedJudge(designatedJudge.trim(), pageable);

            log.info("按指定法官搜索成功，指定法官：{}，结果数量：{}", designatedJudge, casePage.getTotalElements());
            return casePage.getContent();

        } catch (BusinessException e) {
            log.error("按指定法官搜索失败，业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("按指定法官搜索失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("按指定法官搜索失败");
        }
    }
}
