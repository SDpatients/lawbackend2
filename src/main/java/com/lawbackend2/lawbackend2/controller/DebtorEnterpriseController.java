package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.DebtorCreateRequest;
import com.lawbackend2.lawbackend2.dto.DebtorEnterpriseResponse;
import com.lawbackend2.lawbackend2.dto.DebtorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.DebtorEnterprise;
import com.lawbackend2.lawbackend2.service.DebtorEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "债务人信息管理")
@RestController
@RequestMapping("/debtor")
@Validated
public class DebtorEnterpriseController {

    private final DebtorEnterpriseService debtorEnterpriseService;

    public DebtorEnterpriseController(DebtorEnterpriseService debtorEnterpriseService) {
        this.debtorEnterpriseService = debtorEnterpriseService;
    }

    @Operation(summary = "创建债务人信息")
    @PostMapping
    public Result<Map<String, Object>> createDebtor(@Valid @RequestBody DebtorCreateRequest request) {
        Long userId = getCurrentUserId();
        DebtorEnterprise debtorEnterprise = debtorEnterpriseService.createDebtor(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("debtorId", debtorEnterprise.getId());

        log.info("创建债务人信息成功, ID: {}", debtorEnterprise.getId());
        return Result.success(data);
    }

    @Operation(summary = "获取债务人详情", description = "返回债务人详细信息，包含案件案号和案件名称")
    @GetMapping("/{debtorId}")
    public Result<DebtorEnterpriseResponse> getDebtorById(@Parameter(description = "债务人ID") @PathVariable Long debtorId) {
        Long userId = getCurrentUserId();
        DebtorEnterpriseResponse debtorEnterprise = debtorEnterpriseService.getDebtorByIdWithCaseInfo(debtorId, userId);
        return Result.success(debtorEnterprise);
    }

    @Operation(summary = "债务人列表(分页)", description = "支持多条件查询债务人信息，返回数据包含案件案号和案件名称")
    @GetMapping("/list")
    public Result<PageResult<DebtorEnterpriseResponse>> getDebtorList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "企业名称") @RequestParam(required = false) String enterpriseName,
            @Parameter(description = "统一社会信用代码") @RequestParam(required = false) String unifiedSocialCreditCode,
            @Parameter(description = "法定代表人") @RequestParam(required = false) String legalRepresentative) {

        Long userId = getCurrentUserId();
        PageResult<DebtorEnterpriseResponse> pageResult = debtorEnterpriseService.getDebtorListWithCaseInfo(pageNum, pageSize, caseId, enterpriseName, unifiedSocialCreditCode, legalRepresentative, userId);

        return Result.success(pageResult);
    }

    @Operation(summary = "更新债务人信息")
    @PutMapping("/{debtorId}")
    public Result<Void> updateDebtor(
            @Parameter(description = "债务人ID") @PathVariable Long debtorId,
            @Valid @RequestBody DebtorUpdateRequest request) {

        debtorEnterpriseService.updateDebtor(debtorId, request);
        log.info("更新债务人信息成功, ID: {}", debtorId);
        return Result.success();
    }

    @Operation(summary = "删除债务人信息")
    @DeleteMapping("/{debtorId}")
    public Result<Void> deleteDebtor(@Parameter(description = "债务人ID") @PathVariable Long debtorId) {
        debtorEnterpriseService.deleteDebtor(debtorId);
        log.info("删除债务人信息成功, ID: {}", debtorId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户ID");
    }
}
