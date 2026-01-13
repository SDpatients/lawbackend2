package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.EquipmentCreateRequest;
import com.lawbackend2.lawbackend2.entity.Equipment;
import com.lawbackend2.lawbackend2.service.EquipmentService;
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
@Tag(name = "设备管理")
@RestController
@RequestMapping("/equipment")
@Validated
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @Operation(summary = "创建设备")
    @PostMapping
    public Result<Map<String, Object>> createEquipment(@Valid @RequestBody EquipmentCreateRequest request) {
        Long equipmentId = equipmentService.createEquipment(request);

        Map<String, Object> data = new HashMap<>();
        data.put("equipmentId", equipmentId);

        return Result.success(data);
    }

    @Operation(summary = "设备列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<Equipment>> getEquipmentList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "财产ID") @RequestParam(required = false) Long propertyId,
            @Parameter(description = "设备类型") @RequestParam(required = false) String equipmentType,
            @Parameter(description = "设备状态") @RequestParam(required = false) String equipmentStatus,
            @Parameter(description = "管理状态") @RequestParam(required = false) String managementStatus) {

        PageResult<Equipment> result = equipmentService.getEquipmentList(pageNum, pageSize, caseId, propertyId, equipmentType, equipmentStatus, managementStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取设备详情")
    @GetMapping("/{equipmentId}")
    public Result<Equipment> getEquipmentDetail(@Parameter(description = "设备ID") @PathVariable Long equipmentId) {
        Equipment equipment = equipmentService.getEquipmentDetail(equipmentId);
        return Result.success(equipment);
    }

    @Operation(summary = "更新设备信息")
    @PutMapping("/{equipmentId}")
    public Result<Void> updateEquipment(
            @Parameter(description = "设备ID") @PathVariable Long equipmentId,
            @Valid @RequestBody EquipmentCreateRequest request) {

        equipmentService.updateEquipment(equipmentId, request);
        return Result.success();
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{equipmentId}")
    public Result<Void> deleteEquipment(@Parameter(description = "设备ID") @PathVariable Long equipmentId) {
        equipmentService.deleteEquipment(equipmentId);
        return Result.success();
    }
}