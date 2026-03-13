package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.ClaimConfirmationCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimConfirmationUpdateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimConfirmation;
import com.lawbackend2.lawbackend2.service.ClaimConfirmationService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "债权确认与异议管理")
@RestController
@RequestMapping("/claim-confirmation")
@Validated
public class ClaimConfirmationController {

    private final ClaimConfirmationService claimConfirmationService;

    public ClaimConfirmationController(ClaimConfirmationService claimConfirmationService) {
        this.claimConfirmationService = claimConfirmationService;
    }

    @Operation(summary = "创建债权确认记录")
    @PostMapping
    public Result<Map<String, Object>> createConfirmation(@Valid @RequestBody ClaimConfirmationCreateRequest request) {
        Long userId = getCurrentUserId();
        ClaimConfirmation claimConfirmation = claimConfirmationService.createConfirmation(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("confirmationId", claimConfirmation.getId());

        return Result.success(data);
    }

    @Operation(summary = "获取债权确认详情")
    @GetMapping("/{confirmationId}")
    public Result<ClaimConfirmation> getConfirmationById(@Parameter(description = "确认记录ID") @PathVariable Long confirmationId) {
        ClaimConfirmation claimConfirmation = claimConfirmationService.getConfirmationById(confirmationId);
        return Result.success(claimConfirmation);
    }

    @Operation(summary = "获取债权的确认记录")
    @GetMapping("/claim/{claimId}")
    public Result<ClaimConfirmation> getConfirmationByClaimId(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        ClaimConfirmation claimConfirmation = claimConfirmationService.getConfirmationByClaimId(claimId);
        return Result.success(claimConfirmation);
    }

    @Operation(summary = "获取案件的确认记录列表(分页)")
    @GetMapping("/case/{caseId}")
    public Result<PageResult<ClaimConfirmation>> getConfirmationListByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "确认状态") @RequestParam(required = false) String confirmationStatus) {

        List<ClaimConfirmation> list = claimConfirmationService.getConfirmationListByCaseId(caseId, pageNum, pageSize, confirmationStatus);
        Long total = claimConfirmationService.getConfirmationCount(caseId, confirmationStatus);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "更新债权确认记录")
    @PutMapping("/{confirmationId}")
    public Result<Void> updateConfirmation(
            @Parameter(description = "确认记录ID") @PathVariable Long confirmationId,
            @Valid @RequestBody ClaimConfirmationUpdateRequest request) {

        claimConfirmationService.updateConfirmation(confirmationId, request);
        return Result.success();
    }

    @Operation(summary = "删除债权确认记录")
    @DeleteMapping("/{confirmationId}")
    public Result<Void> deleteConfirmation(@Parameter(description = "确认记录ID") @PathVariable Long confirmationId) {
        claimConfirmationService.deleteConfirmation(confirmationId);
        return Result.success();
    }

    @Operation(summary = "提交债权人会议表决")
    @PostMapping("/{confirmationId}/vote")
    public Result<Void> submitVote(
            @Parameter(description = "确认记录ID") @PathVariable Long confirmationId,
            @Parameter(description = "表决结果") @RequestParam String voteResult,
            @Parameter(description = "表决说明") @RequestParam(required = false) String voteNotes) {
        Long userId = getCurrentUserId();
        ClaimConfirmationUpdateRequest request = new ClaimConfirmationUpdateRequest();
        request.setVoteResult(voteResult);
        request.setVoteNotes(voteNotes);
        claimConfirmationService.updateConfirmation(confirmationId, request);
        log.info("债权人会议表决提交成功, confirmationId: {}, voteResult: {}", confirmationId, voteResult);
        return Result.success();
    }

    @Operation(summary = "提交债权异议")
    @PostMapping("/{confirmationId}/objection")
    public Result<Void> submitObjection(@Parameter(description = "确认记录ID") @PathVariable Long confirmationId) {
        Long userId = getCurrentUserId();
        claimConfirmationService.submitObjection(confirmationId, userId);
        log.info("债权异议提交成功, confirmationId: {}", confirmationId);
        return Result.success();
    }

    @Operation(summary = "处理异议协商")
    @PostMapping("/{confirmationId}/negotiation")
    public Result<Void> handleNegotiation(
            @Parameter(description = "确认记录ID") @PathVariable Long confirmationId,
            @Parameter(description = "协商结果") @RequestParam String result) {
        Long userId = getCurrentUserId();
        claimConfirmationService.handleNegotiation(confirmationId, result, userId);
        log.info("异议协商处理成功, confirmationId: {}, result: {}", confirmationId, result);
        return Result.success();
    }

    @Operation(summary = "提交法院裁定")
    @PostMapping("/{confirmationId}/court-ruling")
    public Result<Void> submitCourtRuling(@Parameter(description = "确认记录ID") @PathVariable Long confirmationId) {
        Long userId = getCurrentUserId();
        claimConfirmationService.submitCourtRuling(confirmationId, userId);
        log.info("法院裁定提交成功, confirmationId: {}", confirmationId);
        return Result.success();
    }

    @Operation(summary = "更新诉讼状态")
    @PutMapping("/{confirmationId}/lawsuit-status")
    public Result<Void> updateLawsuitStatus(
            @Parameter(description = "确认记录ID") @PathVariable Long confirmationId,
            @Parameter(description = "诉讼状态") @RequestParam String status) {
        Long userId = getCurrentUserId();
        claimConfirmationService.updateLawsuitStatus(confirmationId, status, userId);
        log.info("诉讼状态更新成功, confirmationId: {}, status: {}", confirmationId, status);
        return Result.success();
    }

    @Operation(summary = "最终确认债权")
    @PostMapping("/{confirmationId}/finalize")
    public Result<Void> finalizeConfirmation(@Parameter(description = "确认记录ID") @PathVariable Long confirmationId) {
        Long userId = getCurrentUserId();
        claimConfirmationService.finalizeConfirmation(confirmationId, userId);
        log.info("债权最终确认成功, confirmationId: {}", confirmationId);
        return Result.success();
    }

    @Operation(summary = "获取有异议的债权")
    @GetMapping("/objections/{caseId}")
    public Result<List<ClaimConfirmation>> getObjectionsByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        List<ClaimConfirmation> list = claimConfirmationService.getObjectionsByCaseId(caseId);
        return Result.success(list);
    }

    @Operation(summary = "获取有诉讼的债权")
    @GetMapping("/lawsuits/{caseId}")
    public Result<List<ClaimConfirmation>> getLawsuitsByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        List<ClaimConfirmation> list = claimConfirmationService.getLawsuitsByCaseId(caseId);
        return Result.success(list);
    }

    @Operation(summary = "获取待确认的债权")
    @GetMapping("/pending/{caseId}")
    public Result<List<ClaimConfirmation>> getPendingConfirmations(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        List<ClaimConfirmation> list = claimConfirmationService.getPendingConfirmations(caseId);
        return Result.success(list);
    }

    @Operation(summary = "获取确认统计")
    @GetMapping("/statistics/{caseId}")
    public Result<Map<String, Object>> getConfirmationStatistics(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        
        Long totalCount = claimConfirmationService.getConfirmationCount(caseId, null);
        Long pendingCount = claimConfirmationService.getConfirmationCount(caseId, "PENDING");
        Long confirmedCount = claimConfirmationService.getConfirmationCount(caseId, "CONFIRMED");
        Long objectionCount = claimConfirmationService.getConfirmationCount(caseId, "OBJECTION");
        Long courtCount = claimConfirmationService.getConfirmationCount(caseId, "COURT");
        Long lawsuitCount = claimConfirmationService.getConfirmationCount(caseId, "LAWSUIT");

        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", totalCount);
        data.put("pendingCount", pendingCount);
        data.put("confirmedCount", confirmedCount);
        data.put("objectionCount", objectionCount);
        data.put("courtCount", courtCount);
        data.put("lawsuitCount", lawsuitCount);

        return Result.success(data);
    }
    
    @Operation(summary = "获取确认记录列表")
    @GetMapping("/list")
    public Result<PageResult<ClaimConfirmation>> getConfirmationList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "确认状态") @RequestParam(required = false) String confirmationStatus) {

        List<ClaimConfirmation> list = claimConfirmationService.getConfirmationListByCaseId(null, pageNum, pageSize, confirmationStatus);
        Long total = claimConfirmationService.getConfirmationCount(null, confirmationStatus);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "同步债权审查数据到确认记录")
    @PostMapping("/{confirmationId}/sync-review-data")
    public Result<Void> syncReviewData(
            @Parameter(description = "确认记录 ID") @PathVariable Long confirmationId) {
        Long userId = getCurrentUserId();
        claimConfirmationService.syncReviewDataToConfirmation(confirmationId, userId);
        log.info("同步债权审查数据成功，confirmationId: {}", confirmationId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
