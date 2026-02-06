package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.ClaimDetailResponse;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationCreateRequest;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationUpdateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelImportResponse;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.service.ClaimRegistrationService;
import com.lawbackend2.lawbackend2.service.ExcelParseService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "债权申报登记管理")
@RestController
@RequestMapping("/claim-registration")
@Validated
public class ClaimRegistrationController {

    private final ClaimRegistrationService claimRegistrationService;
    private final ExcelParseService excelParseService;

    public ClaimRegistrationController(ClaimRegistrationService claimRegistrationService, ExcelParseService excelParseService) {
        this.claimRegistrationService = claimRegistrationService;
        this.excelParseService = excelParseService;
    }

    @Operation(summary = "创建债权申报登记")
    @PostMapping
    public Result<Map<String, Object>> createClaim(@Valid @RequestBody ClaimRegistrationCreateRequest request) {
        Long userId = getCurrentUserId();
        ClaimRegistration claimRegistration = claimRegistrationService.createClaim(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("claimId", claimRegistration.getId());
        data.put("claimNo", claimRegistration.getClaimNo());

        return Result.success(data);
    }

    @Operation(summary = "获取债权申报详情")
    @GetMapping("/{claimId}")
    public Result<ClaimDetailResponse> getClaimById(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        ClaimDetailResponse claimDetail = claimRegistrationService.getClaimDetailById(claimId);
        return Result.success(claimDetail);
    }

    @Operation(summary = "获取债权申报基本信息")
    @GetMapping("/{claimId}/basic")
    public Result<ClaimRegistration> getClaimBasicInfo(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        ClaimRegistration claimRegistration = claimRegistrationService.getClaimById(claimId);
        return Result.success(claimRegistration);
    }

    @Operation(summary = "债权申报列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<ClaimRegistration>> getClaimList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "登记状态") @RequestParam(required = false) String registrationStatus) {

        List<ClaimRegistration> list = claimRegistrationService.getClaimList(pageNum, pageSize, caseId, registrationStatus);
        Long total = claimRegistrationService.getClaimCount(caseId, registrationStatus);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "更新债权申报")
    @PutMapping("/{claimId}")
    public Result<Void> updateClaim(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Valid @RequestBody ClaimRegistrationUpdateRequest request) {

        claimRegistrationService.updateClaim(claimId, request);
        return Result.success();
    }

    @Operation(summary = "删除债权申报")
    @DeleteMapping("/{claimId}")
    public Result<Void> deleteClaim(@Parameter(description = "债权申报ID") @PathVariable Long claimId) {
        Long userId = getCurrentUserId();
        claimRegistrationService.deleteClaim(claimId, userId);
        return Result.success();
    }

    @Operation(summary = "更新债权申报状态")
    @PutMapping("/{claimId}/status")
    public Result<Void> updateRegistrationStatus(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Parameter(description = "状态") @RequestParam String status) {
        Long userId = getCurrentUserId();
        claimRegistrationService.updateRegistrationStatus(claimId, status, userId);
        return Result.success();
    }

    @Operation(summary = "驳回债权申报")
    @PutMapping("/{claimId}/reject")
    public Result<Void> rejectClaim(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Parameter(description = "驳回理由") @RequestParam String rejectReason) {
        Long userId = getCurrentUserId();
        claimRegistrationService.rejectClaim(claimId, rejectReason, userId);
        return Result.success();
    }

    @Operation(summary = "接收申报材料")
    @PostMapping("/{claimId}/material")
    public Result<Void> receiveMaterial(
            @Parameter(description = "债权申报ID") @PathVariable Long claimId,
            @Parameter(description = "接收人") @RequestParam String receiver,
            @Parameter(description = "材料完整性") @RequestParam String completeness) {
        Long userId = getCurrentUserId();
        claimRegistrationService.receiveMaterial(claimId, receiver, completeness, userId);
        return Result.success();
    }

    @Operation(summary = "Excel导入债权登记")
    @PostMapping("/import")
    public Result<ExcelImportResponse> importFromExcel(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId) {
        Long userId = getCurrentUserId();
        ExcelImportResponse response = claimRegistrationService.importFromExcel(file, caseId, userId);
        return Result.success(response);
    }

    @Operation(summary = "EasyExcel导入债权登记")
    @PostMapping("/import-easy")
    public Result<ExcelImportResponse> importFromExcelEasy(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId) {
        Long userId = getCurrentUserId();
        ExcelImportResponse response = claimRegistrationService.importFromExcelEasy(file, caseId, userId);
        return Result.success(response);
    }

    @Operation(summary = "导入已申报债权登记簿格式")
    @PostMapping("/import-declared-claims")
    public Result<ExcelImportResponse> importFromDeclaredClaimsRegister(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "案件ID") @RequestParam("caseId") Long caseId) {
        Long userId = getCurrentUserId();
        ExcelImportResponse response = claimRegistrationService.importFromDeclaredClaimsRegister(file, caseId, userId);
        return Result.success(response);
    }

    @Operation(summary = "解析Excel文件并返回字段映射")
    @PostMapping("/parse-excel")
    public Result<Map<String, Object>> parseExcel(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Sheet索引（从0开始）") @RequestParam(value = "sheetIndex", required = false, defaultValue = "0") Integer sheetIndex,
            @Parameter(description = "模板编码") @RequestParam(value = "templateCode", required = false) String templateCode) {
        try {
            Map<String, Object> parsedData = excelParseService.parseExcelWithSheet(file, sheetIndex, templateCode);
            return Result.success(parsedData);
        } catch (Exception e) {
            log.error("解析Excel失败", e);
            return Result.error("解析Excel失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导出债权登记到Excel")
    @GetMapping("/export")
    public void exportToExcel(
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "登记状态") @RequestParam(required = false) String registrationStatus,
            HttpServletResponse response) {
        claimRegistrationService.exportToExcel(response, caseId, registrationStatus);
    }

    @Operation(summary = "下载Excel导入模板")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = "债权登记导入模板";
            String encodedFileName = java.net.URLEncoder.encode(fileName, java.nio.charset.StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), com.lawbackend2.lawbackend2.dto.ClaimRegistrationExcelDTO.class)
                    .sheet("债权登记")
                    .doWrite(new java.util.ArrayList<>());

            log.info("下载Excel模板成功");
        } catch (Exception e) {
            log.error("下载Excel模板失败", e);
            throw new com.lawbackend2.lawbackend2.exception.BusinessException("下载Excel模板失败: " + e.getMessage());
        }
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
