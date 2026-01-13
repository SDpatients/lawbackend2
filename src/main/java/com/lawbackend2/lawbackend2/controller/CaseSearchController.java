package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.service.CaseSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/case-search")
@Tag(name = "案件搜索", description = "案件搜索相关接口")
public class CaseSearchController {

    private static final Logger log = LoggerFactory.getLogger(CaseSearchController.class);
    
    @Autowired
    private CaseSearchService caseSearchService;

    @GetMapping("/keyword")
    @Operation(summary = "关键词搜索案件", description = "根据关键词搜索案件，支持案件编号、案件名称、受理法院、指定机构、主要负责人、案件来源、案件原因、指定法官等多字段搜索")
    public Result<List<BankruptCase>> searchByKeyword(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("关键词搜索案件，关键词：{}，page：{}，size：{}", keyword, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByKeyword(page, size, keyword);
            log.info("关键词搜索案件成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("关键词搜索案件失败：{}", e.getMessage(), e);
            return Result.error("关键词搜索案件失败：" + e.getMessage());
        }
    }

    @GetMapping("/keyword-and-status")
    @Operation(summary = "关键词和状态搜索案件", description = "根据关键词和案件状态搜索案件")
    public Result<List<BankruptCase>> searchByKeywordAndStatus(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "案件状态") @RequestParam String caseStatus,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("关键词和状态搜索案件，关键词：{}，案件状态：{}，page：{}，size：{}", keyword, caseStatus, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByKeywordAndStatus(page, size, keyword, caseStatus);
            log.info("关键词和状态搜索案件成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("关键词和状态搜索案件失败：{}", e.getMessage(), e);
            return Result.error("关键词和状态搜索案件失败：" + e.getMessage());
        }
    }

    @GetMapping("/keyword-and-progress")
    @Operation(summary = "关键词和进度搜索案件", description = "根据关键词和案件进度搜索案件")
    public Result<List<BankruptCase>> searchByKeywordAndProgress(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "案件进度") @RequestParam String caseProgress,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("关键词和进度搜索案件，关键词：{}，案件进度：{}，page：{}，size：{}", keyword, caseProgress, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByKeywordAndProgress(page, size, keyword, caseProgress);
            log.info("关键词和进度搜索案件成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("关键词和进度搜索案件失败：{}", e.getMessage(), e);
            return Result.error("关键词和进度搜索案件失败：" + e.getMessage());
        }
    }

    @GetMapping("/keyword-and-status-and-progress")
    @Operation(summary = "关键词、状态和进度搜索案件", description = "根据关键词、案件状态和案件进度搜索案件")
    public Result<List<BankruptCase>> searchByKeywordAndStatusAndProgress(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "案件状态") @RequestParam String caseStatus,
            @Parameter(description = "案件进度") @RequestParam String caseProgress,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("关键词、状态和进度搜索案件，关键词：{}，案件状态：{}，案件进度：{}，page：{}，size：{}", keyword, caseStatus, caseProgress, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByKeywordAndStatusAndProgress(page, size, keyword, caseStatus, caseProgress);
            log.info("关键词、状态和进度搜索案件成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("关键词、状态和进度搜索案件失败：{}", e.getMessage(), e);
            return Result.error("关键词、状态和进度搜索案件失败：" + e.getMessage());
        }
    }

    @GetMapping("/case-number")
    @Operation(summary = "按案件编号搜索", description = "根据案件编号搜索案件")
    public Result<List<BankruptCase>> searchByCaseNumber(
            @Parameter(description = "案件编号") @RequestParam String caseNumber,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按案件编号搜索，案件编号：{}，page：{}，size：{}", caseNumber, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByCaseNumber(page, size, caseNumber);
            log.info("按案件编号搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按案件编号搜索失败：{}", e.getMessage(), e);
            return Result.error("按案件编号搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/case-name")
    @Operation(summary = "按案件名称搜索", description = "根据案件名称搜索案件")
    public Result<List<BankruptCase>> searchByCaseName(
            @Parameter(description = "案件名称") @RequestParam String caseName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按案件名称搜索，案件名称：{}，page：{}，size：{}", caseName, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByCaseName(page, size, caseName);
            log.info("按案件名称搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按案件名称搜索失败：{}", e.getMessage(), e);
            return Result.error("按案件名称搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/acceptance-court")
    @Operation(summary = "按受理法院搜索", description = "根据受理法院搜索案件")
    public Result<List<BankruptCase>> searchByAcceptanceCourt(
            @Parameter(description = "受理法院") @RequestParam String acceptanceCourt,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按受理法院搜索，受理法院：{}，page：{}，size：{}", acceptanceCourt, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByAcceptanceCourt(page, size, acceptanceCourt);
            log.info("按受理法院搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按受理法院搜索失败：{}", e.getMessage(), e);
            return Result.error("按受理法院搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/designated-institution")
    @Operation(summary = "按指定机构搜索", description = "根据指定机构搜索案件")
    public Result<List<BankruptCase>> searchByDesignatedInstitution(
            @Parameter(description = "指定机构") @RequestParam String designatedInstitution,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按指定机构搜索，指定机构：{}，page：{}，size：{}", designatedInstitution, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByDesignatedInstitution(page, size, designatedInstitution);
            log.info("按指定机构搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按指定机构搜索失败：{}", e.getMessage(), e);
            return Result.error("按指定机构搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/main-responsible-person")
    @Operation(summary = "按主要负责人搜索", description = "根据主要负责人搜索案件")
    public Result<List<BankruptCase>> searchByMainResponsiblePerson(
            @Parameter(description = "主要负责人") @RequestParam String mainResponsiblePerson,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按主要负责人搜索，主要负责人：{}，page：{}，size：{}", mainResponsiblePerson, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByMainResponsiblePerson(page, size, mainResponsiblePerson);
            log.info("按主要负责人搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按主要负责人搜索失败：{}", e.getMessage(), e);
            return Result.error("按主要负责人搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/acceptance-date-range")
    @Operation(summary = "按受理日期范围搜索", description = "根据受理日期范围搜索案件")
    public Result<List<BankruptCase>> searchByAcceptanceDateRange(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按受理日期范围搜索，开始日期：{}，结束日期：{}，page：{}，size：{}", startDate, endDate, page, size);

        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<BankruptCase> caseList = caseSearchService.searchByAcceptanceDateRange(page, size, start, end);
            log.info("按受理日期范围搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按受理日期范围搜索失败：{}", e.getMessage(), e);
            return Result.error("按受理日期范围搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/case-source")
    @Operation(summary = "按案件来源搜索", description = "根据案件来源搜索案件")
    public Result<List<BankruptCase>> searchByCaseSource(
            @Parameter(description = "案件来源") @RequestParam String caseSource,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按案件来源搜索，案件来源：{}，page：{}，size：{}", caseSource, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByCaseSource(page, size, caseSource);
            log.info("按案件来源搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按案件来源搜索失败：{}", e.getMessage(), e);
            return Result.error("按案件来源搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/case-reason")
    @Operation(summary = "按案件原因搜索", description = "根据案件原因搜索案件")
    public Result<List<BankruptCase>> searchByCaseReason(
            @Parameter(description = "案件原因") @RequestParam String caseReason,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按案件原因搜索，案件原因：{}，page：{}，size：{}", caseReason, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByCaseReason(page, size, caseReason);
            log.info("按案件原因搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按案件原因搜索失败：{}", e.getMessage(), e);
            return Result.error("按案件原因搜索失败：" + e.getMessage());
        }
    }

    @GetMapping("/designated-judge")
    @Operation(summary = "按指定法官搜索", description = "根据指定法官搜索案件")
    public Result<List<BankruptCase>> searchByDesignatedJudge(
            @Parameter(description = "指定法官") @RequestParam String designatedJudge,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        log.info("按指定法官搜索，指定法官：{}，page：{}，size：{}", designatedJudge, page, size);

        try {
            List<BankruptCase> caseList = caseSearchService.searchByDesignatedJudge(page, size, designatedJudge);
            log.info("按指定法官搜索成功");
            return Result.success(caseList);

        } catch (Exception e) {
            log.error("按指定法官搜索失败：{}", e.getMessage(), e);
            return Result.error("按指定法官搜索失败：" + e.getMessage());
        }
    }
}
