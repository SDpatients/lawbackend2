package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.IntellectualPropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.IntellectualProperty;
import com.lawbackend2.lawbackend2.service.IntellectualPropertyService;
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
@Tag(name = "知识产权管理")
@RestController
@RequestMapping("/intellectual-property")
@Validated
public class IntellectualPropertyController {

    private final IntellectualPropertyService intellectualPropertyService;

    public IntellectualPropertyController(IntellectualPropertyService intellectualPropertyService) {
        this.intellectualPropertyService = intellectualPropertyService;
    }

    @Operation(summary = "创建知识产权")
    @PostMapping
    public Result<Map<String, Object>> createIntellectualProperty(@Valid @RequestBody IntellectualPropertyCreateRequest request) {
        Long ipId = intellectualPropertyService.createIntellectualProperty(request);

        Map<String, Object> data = new HashMap<>();
        data.put("ipId", ipId);

        return Result.success(data);
    }

    @Operation(summary = "知识产权列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<IntellectualProperty>> getIntellectualPropertyList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "财产ID") @RequestParam(required = false) Long propertyId,
            @Parameter(description = "知识产权类型") @RequestParam(required = false) String ipType,
            @Parameter(description = "知识产权状态") @RequestParam(required = false) String ipStatus,
            @Parameter(description = "管理状态") @RequestParam(required = false) String managementStatus) {

        PageResult<IntellectualProperty> result = intellectualPropertyService.getIntellectualPropertyList(pageNum, pageSize, caseId, propertyId, ipType, ipStatus, managementStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取知识产权详情")
    @GetMapping("/{ipId}")
    public Result<IntellectualProperty> getIntellectualPropertyDetail(@Parameter(description = "知识产权ID") @PathVariable Long ipId) {
        IntellectualProperty ip = intellectualPropertyService.getIntellectualPropertyDetail(ipId);
        return Result.success(ip);
    }

    @Operation(summary = "更新知识产权信息")
    @PutMapping("/{ipId}")
    public Result<Void> updateIntellectualProperty(
            @Parameter(description = "知识产权ID") @PathVariable Long ipId,
            @Valid @RequestBody IntellectualPropertyCreateRequest request) {

        intellectualPropertyService.updateIntellectualProperty(ipId, request);
        return Result.success();
    }

    @Operation(summary = "删除知识产权")
    @DeleteMapping("/{ipId}")
    public Result<Void> deleteIntellectualProperty(@Parameter(description = "知识产权ID") @PathVariable Long ipId) {
        intellectualPropertyService.deleteIntellectualProperty(ipId);
        return Result.success();
    }
}