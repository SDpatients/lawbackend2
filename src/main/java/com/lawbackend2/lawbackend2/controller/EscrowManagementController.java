package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.EscrowManagementCreateRequest;
import com.lawbackend2.lawbackend2.dto.request.EscrowManagementReleaseRequest;
import com.lawbackend2.lawbackend2.entity.EscrowManagement;
import com.lawbackend2.lawbackend2.service.EscrowManagementService;
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
@Tag(name = "提存管理")
@RestController
@RequestMapping("/escrow-management")
@Validated
public class EscrowManagementController {

    private final EscrowManagementService escrowManagementService;

    public EscrowManagementController(EscrowManagementService escrowManagementService) {
        this.escrowManagementService = escrowManagementService;
    }

    @Operation(summary = "创建提存记录")
    @PostMapping
    public Result<Map<String, Object>> createEscrowManagement(@Valid @RequestBody EscrowManagementCreateRequest request) {
        Long escrowId = escrowManagementService.createEscrowManagement(request);

        Map<String, Object> data = new HashMap<>();
        data.put("escrowId", escrowId);

        return Result.success(data);
    }

    @Operation(summary = "提存记录列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<EscrowManagement>> getEscrowManagementList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "提存类型") @RequestParam(required = false) String escrowType,
            @Parameter(description = "释放状态") @RequestParam(required = false) String releaseStatus) {

        PageResult<EscrowManagement> result = escrowManagementService.getEscrowManagementList(pageNum, pageSize, caseId, escrowType, releaseStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取提存记录详情")
    @GetMapping("/{escrowId}")
    public Result<EscrowManagement> getEscrowManagementDetail(@Parameter(description = "提存ID") @PathVariable Long escrowId) {
        EscrowManagement escrow = escrowManagementService.getEscrowManagementDetail(escrowId);
        return Result.success(escrow);
    }

    @Operation(summary = "释放提存")
    @PutMapping("/{escrowId}/release")
    public Result<Void> releaseEscrow(
            @Parameter(description = "提存ID") @PathVariable Long escrowId,
            @Valid @RequestBody EscrowManagementReleaseRequest request) {

        escrowManagementService.releaseEscrow(escrowId, request);
        return Result.success();
    }

    @Operation(summary = "删除提存记录")
    @DeleteMapping("/{escrowId}")
    public Result<Void> deleteEscrowManagement(@Parameter(description = "提存ID") @PathVariable Long escrowId) {
        escrowManagementService.deleteEscrowManagement(escrowId);
        return Result.success();
    }
}