package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.PropertyCreateRequest;
import com.lawbackend2.lawbackend2.entity.Property;
import com.lawbackend2.lawbackend2.service.PropertyService;
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
@Tag(name = "财产管理")
@RestController
@RequestMapping("/property")
@Validated
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Operation(summary = "创建财产")
    @PostMapping
    public Result<Map<String, Object>> createProperty(@Valid @RequestBody PropertyCreateRequest request) {
        Long propertyId = propertyService.createProperty(request);

        Map<String, Object> data = new HashMap<>();
        data.put("propertyId", propertyId);

        return Result.success(data);
    }

    @Operation(summary = "财产列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<Property>> getPropertyList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "财产类型") @RequestParam(required = false) String propertyType,
            @Parameter(description = "财产状态") @RequestParam(required = false) String propertyStatus,
            @Parameter(description = "管理状态") @RequestParam(required = false) String managementStatus) {

        PageResult<Property> result = propertyService.getPropertyList(pageNum, pageSize, caseId, propertyType, propertyStatus, managementStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取财产详情")
    @GetMapping("/{propertyId}")
    public Result<Property> getPropertyDetail(@Parameter(description = "财产ID") @PathVariable Long propertyId) {
        Property property = propertyService.getPropertyDetail(propertyId);
        return Result.success(property);
    }

    @Operation(summary = "更新财产信息")
    @PutMapping("/{propertyId}")
    public Result<Void> updateProperty(
            @Parameter(description = "财产ID") @PathVariable Long propertyId,
            @Valid @RequestBody PropertyCreateRequest request) {

        propertyService.updateProperty(propertyId, request);
        return Result.success();
    }

    @Operation(summary = "删除财产")
    @DeleteMapping("/{propertyId}")
    public Result<Void> deleteProperty(@Parameter(description = "财产ID") @PathVariable Long propertyId) {
        propertyService.deleteProperty(propertyId);
        return Result.success();
    }
}