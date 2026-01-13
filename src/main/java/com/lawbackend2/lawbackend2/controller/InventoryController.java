package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.request.InventoryCreateRequest;
import com.lawbackend2.lawbackend2.entity.Inventory;
import com.lawbackend2.lawbackend2.service.InventoryService;
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
@Tag(name = "存货管理")
@RestController
@RequestMapping("/inventory")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "创建存货")
    @PostMapping
    public Result<Map<String, Object>> createInventory(@Valid @RequestBody InventoryCreateRequest request) {
        Long inventoryId = inventoryService.createInventory(request);

        Map<String, Object> data = new HashMap<>();
        data.put("inventoryId", inventoryId);

        return Result.success(data);
    }

    @Operation(summary = "存货列表(分页)")
    @GetMapping("/list")
    public Result<PageResult<Inventory>> getInventoryList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "财产ID") @RequestParam(required = false) Long propertyId,
            @Parameter(description = "存货类型") @RequestParam(required = false) String inventoryType,
            @Parameter(description = "存货状态") @RequestParam(required = false) String inventoryStatus,
            @Parameter(description = "管理状态") @RequestParam(required = false) String managementStatus) {

        PageResult<Inventory> result = inventoryService.getInventoryList(pageNum, pageSize, caseId, propertyId, inventoryType, inventoryStatus, managementStatus);
        return Result.success(result);
    }

    @Operation(summary = "获取存货详情")
    @GetMapping("/{inventoryId}")
    public Result<Inventory> getInventoryDetail(@Parameter(description = "存货ID") @PathVariable Long inventoryId) {
        Inventory inventory = inventoryService.getInventoryDetail(inventoryId);
        return Result.success(inventory);
    }

    @Operation(summary = "更新存货信息")
    @PutMapping("/{inventoryId}")
    public Result<Void> updateInventory(
            @Parameter(description = "存货ID") @PathVariable Long inventoryId,
            @Valid @RequestBody InventoryCreateRequest request) {

        inventoryService.updateInventory(inventoryId, request);
        return Result.success();
    }

    @Operation(summary = "删除存货")
    @DeleteMapping("/{inventoryId}")
    public Result<Void> deleteInventory(@Parameter(description = "存货ID") @PathVariable Long inventoryId) {
        inventoryService.deleteInventory(inventoryId);
        return Result.success();
    }
}