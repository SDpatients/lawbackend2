package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.VehicleCreateRequest;
import com.lawbackend2.lawbackend2.entity.Vehicle;
import com.lawbackend2.lawbackend2.service.VehicleService;
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
@Tag(name = "车辆管理")
@RestController
@RequestMapping("/vehicle")
@Validated
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "创建车辆")
    @PostMapping
    public Result<Map<String, Object>> createVehicle(@Valid @RequestBody VehicleCreateRequest request) {
        Long vehicleId = vehicleService.createVehicle(request);

        Map<String, Object> data = new HashMap<>();
        data.put("vehicleId", vehicleId);

        return Result.success(data);
    }

    @Operation(summary = "车辆列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<Vehicle>> getVehicleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "财产ID") @RequestParam(required = false) Long propertyId,
            @Parameter(description = "车辆类型") @RequestParam(required = false) String vehicleType,
            @Parameter(description = "车辆状态") @RequestParam(required = false) String vehicleStatus,
            @Parameter(description = "管理状态") @RequestParam(required = false) String managementStatus) {

        PageResult<Vehicle> result = vehicleService.getVehicleList(pageNum, pageSize, caseId, propertyId, vehicleType, vehicleStatus, managementStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取车辆详情")
    @GetMapping("/{vehicleId}")
    public Result<Vehicle> getVehicleDetail(@Parameter(description = "车辆ID") @PathVariable Long vehicleId) {
        Vehicle vehicle = vehicleService.getVehicleDetail(vehicleId);
        return Result.success(vehicle);
    }

    @Operation(summary = "更新车辆信息")
    @PutMapping("/{vehicleId}")
    public Result<Void> updateVehicle(
            @Parameter(description = "车辆ID") @PathVariable Long vehicleId,
            @Valid @RequestBody VehicleCreateRequest request) {

        vehicleService.updateVehicle(vehicleId, request);
        return Result.success();
    }

    @Operation(summary = "删除车辆")
    @DeleteMapping("/{vehicleId}")
    public Result<Void> deleteVehicle(@Parameter(description = "车辆ID") @PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return Result.success();
    }
}