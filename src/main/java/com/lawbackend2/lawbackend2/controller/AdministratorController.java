package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.AdministratorCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorStaffCreateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorStaffUpdateRequest;
import com.lawbackend2.lawbackend2.dto.AdministratorUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Administrator;
import com.lawbackend2.lawbackend2.entity.AdministratorStaff;
import com.lawbackend2.lawbackend2.service.AdministratorService;
import com.lawbackend2.lawbackend2.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "管理人信息管理")
@RestController
@RequestMapping("/administrator")
@Validated
public class AdministratorController {

    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @Operation(summary = "创建管理人信息")
    @PostMapping
    public Result<Map<String, Object>> createAdministrator(@Valid @RequestBody AdministratorCreateRequest request) {
        Long userId = getCurrentUserId();
        Administrator administrator = administratorService.createAdministrator(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("administratorId", administrator.getId());

        log.info("创建管理人信息成功, ID: {}", administrator.getId());
        return Result.success(data);
    }

    @Operation(summary = "管理人列表")
    @GetMapping("/list")
    public Result<PageResult<Administrator>> getAdministratorList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "管理人名称（模糊查询）") @RequestParam(required = false) String administratorName) {

        List<Administrator> list;
        Long total;
        if (administratorName != null && !administratorName.trim().isEmpty()) {
            list = administratorService.getAdministratorList(pageNum, pageSize, caseId, administratorName);
            total = administratorService.getAdministratorCount(caseId, administratorName);
        } else {
            list = administratorService.getAdministratorList(pageNum, pageSize, caseId);
            total = administratorService.getAdministratorCount(caseId);
        }

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "获取管理人详情")
    @GetMapping("/{administratorId}")
    public Result<Administrator> getAdministratorById(@Parameter(description = "管理人ID") @PathVariable Long administratorId) {
        Administrator administrator = administratorService.getAdministratorById(administratorId);
        return Result.success(administrator);
    }

    @Operation(summary = "更新管理人信息")
    @PutMapping("/{administratorId}")
    public Result<Void> updateAdministrator(
            @Parameter(description = "管理人ID") @PathVariable Long administratorId,
            @Valid @RequestBody AdministratorUpdateRequest request) {

        administratorService.updateAdministrator(administratorId, request);
        log.info("更新管理人信息成功, ID: {}", administratorId);
        return Result.success();
    }

    @Operation(summary = "添加管理人员工")
    @PostMapping("/{administratorId}/staff")
    public Result<Map<String, Object>> createAdministratorStaff(
            @Parameter(description = "管理人ID") @PathVariable Long administratorId,
            @Valid @RequestBody AdministratorStaffCreateRequest request) {

        Long userId = getCurrentUserId();
        AdministratorStaff staff = administratorService.createAdministratorStaff(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("staffId", staff.getId());

        log.info("创建管理人员工成功, ID: {}", staff.getId());
        return Result.success(data);
    }

    @Operation(summary = "员工列表")
    @GetMapping("/{administratorId}/staff/list")
    public Result<List<AdministratorStaff>> getAdministratorStaffList(
            @Parameter(description = "管理人ID") @PathVariable Long administratorId) {

        List<AdministratorStaff> list = administratorService.getAdministratorStaffList(administratorId);
        return Result.success(list);
    }

    @Operation(summary = "获取员工详情")
    @GetMapping("/{administratorId}/staff/{staffId}")
    public Result<AdministratorStaff> getAdministratorStaffById(
            @Parameter(description = "管理人ID") @PathVariable Long administratorId,
            @Parameter(description = "员工ID") @PathVariable Long staffId) {

        AdministratorStaff staff = administratorService.getAdministratorStaffById(staffId);
        return Result.success(staff);
    }

    @Operation(summary = "删除管理人信息")
    @DeleteMapping("/{administratorId}")
    public Result<Void> deleteAdministrator(@Parameter(description = "管理人ID") @PathVariable Long administratorId) {
        administratorService.deleteAdministrator(administratorId);
        log.info("删除管理人信息成功, ID: {}", administratorId);
        return Result.success();
    }

    @Operation(summary = "删除管理人员工信息")
    @DeleteMapping("/{administratorId}/staff/{staffId}")
    public Result<Void> deleteAdministratorStaff(
            @Parameter(description = "管理人ID") @PathVariable Long administratorId,
            @Parameter(description = "员工ID") @PathVariable Long staffId) {
        administratorService.deleteAdministratorStaff(staffId);
        log.info("删除管理人员工信息成功, ID: {}", staffId);
        return Result.success();
    }

    @Operation(summary = "更新管理人员工信息")
    @PutMapping("/{administratorId}/staff/{staffId}")
    public Result<Void> updateAdministratorStaff(
            @Parameter(description = "管理人ID") @PathVariable Long administratorId,
            @Parameter(description = "员工ID") @PathVariable Long staffId,
            @Valid @RequestBody AdministratorStaffUpdateRequest request) {
        administratorService.updateAdministratorStaff(staffId, request);
        log.info("更新管理人员工信息成功, ID: {}", staffId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
