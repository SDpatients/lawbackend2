package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.DistributionDetailCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.DistributionDetailPaymentRequest;
import com.lawbackend2.lawbackend2.entity.DistributionDetail;
import com.lawbackend2.lawbackend2.service.DistributionDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "分配明细管理")
@RestController
@RequestMapping("/distribution-detail")
@Validated
public class DistributionDetailController {

    private final DistributionDetailService distributionDetailService;

    public DistributionDetailController(DistributionDetailService distributionDetailService) {
        this.distributionDetailService = distributionDetailService;
    }

    @Operation(summary = "创建分配明细")
    @PostMapping
    public Result<Map<String, Object>> createDistributionDetail(@Valid @RequestBody DistributionDetailCreateRequest request) {
        Long detailId = distributionDetailService.createDistributionDetail(request);

        Map<String, Object> data = new HashMap<>();
        data.put("detailId", detailId);

        return Result.success(data);
    }

    @Operation(summary = "分配明细列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<DistributionDetail>> getDistributionDetailList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分配执行ID") @RequestParam(required = false) Long distributionExecutionId,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "债权人类型") @RequestParam(required = false) String creditorType,
            @Parameter(description = "支付状态") @RequestParam(required = false) String paymentStatus) {

        PageResult<DistributionDetail> result = distributionDetailService.getDistributionDetailList(pageNum, pageSize, distributionExecutionId, caseId, creditorType, paymentStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取分配明细详情")
    @GetMapping("/{detailId}")
    public Result<DistributionDetail> getDistributionDetailDetail(@Parameter(description = "明细ID") @PathVariable Long detailId) {
        DistributionDetail detail = distributionDetailService.getDistributionDetailDetail(detailId);
        return Result.success(detail);
    }

    @Operation(summary = "支付分配明细")
    @PutMapping("/{detailId}/payment")
    public Result<Void> payDistributionDetail(
            @Parameter(description = "明细ID") @PathVariable Long detailId,
            @Valid @RequestBody DistributionDetailPaymentRequest request) {

        distributionDetailService.payDistributionDetail(detailId, request);
        return Result.success();
    }

    @Operation(summary = "删除分配明细")
    @DeleteMapping("/{detailId}")
    public Result<Void> deleteDistributionDetail(@Parameter(description = "明细ID") @PathVariable Long detailId) {
        distributionDetailService.deleteDistributionDetail(detailId);
        return Result.success();
    }
}