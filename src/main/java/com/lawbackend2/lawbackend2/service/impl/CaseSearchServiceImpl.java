package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.dto.request.CaseSearchRequest;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.CaseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Override
    public PageResult<BankruptCase> advancedSearch(CaseSearchRequest request) {
        log.info("高级搜索案件，请求参数：{}", request);

        try {
            Sort sort = Sort.by(
                    "DESC".equalsIgnoreCase(request.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                    StringUtils.hasText(request.getSortField()) ? request.getSortField() : "createTime"
            );

            Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

            Specification<BankruptCase> spec = buildSearchSpecification(request);

            Page<BankruptCase> casePage = caseRepository.findAll(spec, pageable);

            log.info("高级搜索案件成功，结果数量：{}", casePage.getTotalElements());
            return PageResult.of(casePage.getTotalElements(), casePage.getContent());

        } catch (Exception e) {
            log.error("高级搜索案件失败，系统异常：{}", e.getMessage(), e);
            throw new BusinessException("高级搜索案件失败：" + e.getMessage());
        }
    }

    private Specification<BankruptCase> buildSearchSpecification(CaseSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().trim() + "%";
                Predicate keywordPredicate = cb.or(
                        cb.like(root.get("caseNumber"), keyword),
                        cb.like(root.get("caseName"), keyword),
                        cb.like(root.get("acceptanceCourt"), keyword),
                        cb.like(root.get("designatedInstitution"), keyword),
                        cb.like(root.get("mainResponsiblePerson"), keyword),
                        cb.like(root.get("caseSource"), keyword),
                        cb.like(root.get("caseReason"), keyword),
                        cb.like(root.get("designatedJudge"), keyword),
                        cb.like(root.get("undertakingPersonnel"), keyword)
                );
                predicates.add(keywordPredicate);
            }

            if (StringUtils.hasText(request.getCaseNumber())) {
                predicates.add(cb.like(root.get("caseNumber"), "%" + request.getCaseNumber().trim() + "%"));
            }

            if (StringUtils.hasText(request.getCaseName())) {
                predicates.add(cb.like(root.get("caseName"), "%" + request.getCaseName().trim() + "%"));
            }

            if (StringUtils.hasText(request.getCaseStatus())) {
                predicates.add(cb.equal(root.get("caseStatus"), request.getCaseStatus()));
            }

            if (StringUtils.hasText(request.getCaseProgress())) {
                predicates.add(cb.equal(root.get("caseProgress"), request.getCaseProgress()));
            }

            if (StringUtils.hasText(request.getAcceptanceCourt())) {
                predicates.add(cb.like(root.get("acceptanceCourt"), "%" + request.getAcceptanceCourt().trim() + "%"));
            }

            if (StringUtils.hasText(request.getDesignatedInstitution())) {
                predicates.add(cb.like(root.get("designatedInstitution"), "%" + request.getDesignatedInstitution().trim() + "%"));
            }

            if (StringUtils.hasText(request.getMainResponsiblePerson())) {
                predicates.add(cb.like(root.get("mainResponsiblePerson"), "%" + request.getMainResponsiblePerson().trim() + "%"));
            }

            if (StringUtils.hasText(request.getCaseSource())) {
                predicates.add(cb.like(root.get("caseSource"), "%" + request.getCaseSource().trim() + "%"));
            }

            if (StringUtils.hasText(request.getCaseReason())) {
                predicates.add(cb.like(root.get("caseReason"), "%" + request.getCaseReason().trim() + "%"));
            }

            if (StringUtils.hasText(request.getDesignatedJudge())) {
                predicates.add(cb.like(root.get("designatedJudge"), "%" + request.getDesignatedJudge().trim() + "%"));
            }

            if (StringUtils.hasText(request.getUndertakingPersonnel())) {
                predicates.add(cb.like(root.get("undertakingPersonnel"), "%" + request.getUndertakingPersonnel().trim() + "%"));
            }

            if (StringUtils.hasText(request.getReviewStatus())) {
                predicates.add(cb.equal(root.get("reviewStatus"), request.getReviewStatus()));
            }

            if (request.getIsSimplifiedTrial() != null) {
                predicates.add(cb.equal(root.get("isSimplifiedTrial"), request.getIsSimplifiedTrial()));
            }

            if (request.getAcceptanceDateStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("acceptanceDate"), request.getAcceptanceDateStart()));
            }

            if (request.getAcceptanceDateEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("acceptanceDate"), request.getAcceptanceDateEnd()));
            }

            if (request.getFilingDateStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("filingDate"), request.getFilingDateStart()));
            }

            if (request.getFilingDateEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("filingDate"), request.getFilingDateEnd()));
            }

            if (request.getCreateDateStart() != null) {
                LocalDateTime startDateTime = request.getCreateDateStart().atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"), startDateTime));
            }

            if (request.getCreateDateEnd() != null) {
                LocalDateTime endDateTime = request.getCreateDateEnd().atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"), endDateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Long countByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByKeyword(keyword.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByKeywordAndStatus(String keyword, String caseStatus) {
        if (!StringUtils.hasText(keyword)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByKeywordAndCaseStatus(keyword.trim(), caseStatus, pageable).getTotalElements();
    }

    @Override
    public Long countByKeywordAndProgress(String keyword, String caseProgress) {
        if (!StringUtils.hasText(keyword)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByKeywordAndCaseProgress(keyword.trim(), caseProgress, pageable).getTotalElements();
    }

    @Override
    public Long countByKeywordAndStatusAndProgress(String keyword, String caseStatus, String caseProgress) {
        if (!StringUtils.hasText(keyword)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByKeywordAndStatusAndProgress(keyword.trim(), caseStatus, caseProgress, pageable).getTotalElements();
    }

    @Override
    public Long countByCaseNumber(String caseNumber) {
        if (!StringUtils.hasText(caseNumber)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByCaseNumber(caseNumber.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByCaseName(String caseName) {
        if (!StringUtils.hasText(caseName)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByCaseName(caseName.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByAcceptanceCourt(String acceptanceCourt) {
        if (!StringUtils.hasText(acceptanceCourt)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByAcceptanceCourt(acceptanceCourt.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByDesignatedInstitution(String designatedInstitution) {
        if (!StringUtils.hasText(designatedInstitution)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByDesignatedInstitution(designatedInstitution.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByMainResponsiblePerson(String mainResponsiblePerson) {
        if (!StringUtils.hasText(mainResponsiblePerson)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByMainResponsiblePerson(mainResponsiblePerson.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByAcceptanceDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByAcceptanceDateRange(startDate, endDate, pageable).getTotalElements();
    }

    @Override
    public Long countByCaseSource(String caseSource) {
        if (!StringUtils.hasText(caseSource)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByCaseSource(caseSource.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByCaseReason(String caseReason) {
        if (!StringUtils.hasText(caseReason)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByCaseReason(caseReason.trim(), pageable).getTotalElements();
    }

    @Override
    public Long countByDesignatedJudge(String designatedJudge) {
        if (!StringUtils.hasText(designatedJudge)) {
            return 0L;
        }
        Pageable pageable = PageRequest.of(0, 1);
        return caseRepository.searchByDesignatedJudge(designatedJudge.trim(), pageable).getTotalElements();
    }
}
