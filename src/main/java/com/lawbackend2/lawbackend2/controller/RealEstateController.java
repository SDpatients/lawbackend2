package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.RealEstateCreateRequest;
import com.lawbackend2.lawbackend2.entity.RealEstate;
import com.lawbackend2.lawbackend2.service.RealEstateService;
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
@Tag(name = "房产管理")
@RestController
@RequestMapping("/real-estate")
@Validated
public class RealEstateController {

    private final RealEstateService realEstateService;

    public RealEstateController(RealEstateService realEstateService) {
        this.realEstateService = realEstateService;
    }

    @Operation(summary = "创建房产")
    @PostMapping
    public Result<Map<String, Object>> createRealEstate(@Valid @RequestBody RealEstateCreateRequest request) {
        Long estateId = realEstateService.createRealEstate(request);

        Map<String, Object> data = new HashMap<>();
        data.put("estateId", estateId);

        return Result.success(data);
    }

    @Operation(summary = "房产列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<RealEstate>> getRealEstateList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "财产ID") @RequestParam(required = false) Long propertyId,
            @Parameter(description = "房产类型") @RequestParam(required = false) String estateType,
            @Parameter(description = "房产状态") @RequestParam(required = false) String estateStatus,
            @Parameter(description = "管理状态") @RequestParam(required = false) String managementStatus) {

        PageResult<RealEstate> result = realEstateService.getRealEstateList(pageNum, pageSize, caseId, propertyId, estateType, estateStatus, managementStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取房产详情")
    @GetMapping("/{estateId}")
    public Result<RealEstate> getRealEstateDetail(@Parameter(description = "房产ID") @PathVariable Long estateId) {
        RealEstate estate = realEstateService.getRealEstateDetail(estateId);
        return Result.success(estate);
    }

    @Operation(summary = "更新房产信息")
    @PutMapping("/{estateId}")
    public Result<Void> updateRealEstate(
            @Parameter(description = "房产ID") @PathVariable Long estateId,
            @Valid @RequestBody RealEstateCreateRequest request) {

        realEstateService.updateRealEstate(estateId, request);
        return Result.success();
    }

    @Operation(summary = "删除房产")
    @DeleteMapping("/{estateId}")
    public Result<Void> deleteRealEstate(@Parameter(description = "房产ID") @PathVariable Long estateId) {
        realEstateService.deleteRealEstate(estateId);
        return Result.success();
    }
}