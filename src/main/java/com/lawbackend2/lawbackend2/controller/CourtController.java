package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CourtCreateRequest;
import com.lawbackend2.lawbackend2.dto.CourtUpdateRequest;
import com.lawbackend2.lawbackend2.entity.Court;
import com.lawbackend2.lawbackend2.service.CourtService;
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
@Tag(name = "法院信息管理")
@RestController
@RequestMapping("/court")
@Validated
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @Operation(summary = "创建法院信息")
    @PostMapping
    public Result<Map<String, Object>> createCourt(@Valid @RequestBody CourtCreateRequest request) {
        Long userId = getCurrentUserId();
        Court court = courtService.createCourt(request, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("courtId", court.getId());

        log.info("创建法院信息成功, ID: {}", court.getId());
        return Result.success(data);
    }

    @Operation(summary = "法院列表")
    @GetMapping("/list")
    public Result<PageResult<Court>> getCourtList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "法院级别") @RequestParam(required = false) String courtLevel,
            @Parameter(description = "法院简称") @RequestParam(required = false) String shortName,
            @Parameter(description = "法院全称") @RequestParam(required = false) String fullName) {

        List<Court> list = courtService.getCourtList(pageNum, pageSize, courtLevel, shortName, fullName);
        Long total = courtService.getCourtCount(courtLevel, shortName, fullName);

        return Result.success(PageResult.of(total, list));
    }

    @Operation(summary = "获取法院详情")
    @GetMapping("/{courtId}")
    public Result<Court> getCourtById(@Parameter(description = "法院ID") @PathVariable Long courtId) {
        Court court = courtService.getCourtById(courtId);
        return Result.success(court);
    }

    @Operation(summary = "更新法院信息")
    @PutMapping("/{courtId}")
    public Result<Void> updateCourt(
            @Parameter(description = "法院ID") @PathVariable Long courtId,
            @Valid @RequestBody CourtUpdateRequest request) {

        courtService.updateCourt(courtId, request);
        log.info("更新法院信息成功, ID: {}", courtId);
        return Result.success();
    }

    @Operation(summary = "删除法院信息")
    @DeleteMapping("/{courtId}")
    public Result<Void> deleteCourt(@Parameter(description = "法院ID") @PathVariable Long courtId) {
        courtService.deleteCourt(courtId);
        log.info("删除法院信息成功, ID: {}", courtId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
