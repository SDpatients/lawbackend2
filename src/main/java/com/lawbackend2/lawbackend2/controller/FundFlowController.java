package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.FundFlowCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowStatusRequest;
import com.lawbackend2.lawbackend2.dto.request.FundFlowUpdateRequest;
import com.lawbackend2.lawbackend2.entity.FundFlow;
import com.lawbackend2.lawbackend2.service.FundFlowService;
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
@Tag(name = "资金流水管理")
@RestController
@RequestMapping("/fund-flow")
@Validated
public class FundFlowController {

    private final FundFlowService fundFlowService;

    public FundFlowController(FundFlowService fundFlowService) {
        this.fundFlowService = fundFlowService;
    }

    @Operation(summary = "创建资金流水")
    @PostMapping
    public Result<Map<String, Object>> createFundFlow(@Valid @RequestBody FundFlowCreateRequest request) {
        Long flowId = fundFlowService.createFundFlow(request);

        Map<String, Object> data = new HashMap<>();
        data.put("flowId", flowId);

        return Result.success(data);
    }

    @Operation(summary = "资金流水列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<com.lawbackend2.lawbackend2.dto.response.FundFlowResponse>> getFundFlowList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "资金账户ID") @RequestParam(required = false) Long fundAccountId,
            @Parameter(description = "流水类型") @RequestParam(required = false) String flowType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        PageResult<com.lawbackend2.lawbackend2.dto.response.FundFlowResponse> result = fundFlowService.getFundFlowList(pageNum, pageSize, caseId, fundAccountId, flowType, status);
        return Result.success(result);
    }

    @Operation(summary = "获取资金流水详情")
    @GetMapping("/{flowId}")
    public Result<FundFlow> getFundFlowDetail(@Parameter(description = "流水ID") @PathVariable Long flowId) {
        FundFlow fundFlow = fundFlowService.getFundFlowDetail(flowId);
        return Result.success(fundFlow);
    }

    @Operation(summary = "更新资金流水信息")
    @PutMapping("/{flowId}")
    public Result<Void> updateFundFlow(
            @Parameter(description = "流水ID") @PathVariable Long flowId,
            @Valid @RequestBody FundFlowUpdateRequest request) {

        fundFlowService.updateFundFlow(flowId, request);
        return Result.success();
    }

    @Operation(summary = "更新资金流水状态")
    @PutMapping("/{flowId}/status")
    public Result<Void> updateFundFlowStatus(
            @Parameter(description = "流水ID") @PathVariable Long flowId,
            @Valid @RequestBody FundFlowStatusRequest request) {

        fundFlowService.updateFundFlowStatus(flowId, request);
        return Result.success();
    }

    @Operation(summary = "删除资金流水")
    @DeleteMapping("/{flowId}")
    public Result<Void> deleteFundFlow(@Parameter(description = "流水ID") @PathVariable Long flowId) {
        fundFlowService.deleteFundFlow(flowId);
        return Result.success();
    }
}
