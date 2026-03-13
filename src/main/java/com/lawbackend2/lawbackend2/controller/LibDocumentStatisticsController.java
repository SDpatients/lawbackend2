package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.response.LibDashboardStatisticsResponse;
import com.lawbackend2.lawbackend2.service.LibDocumentStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文档库统计管理")
@RestController
@RequestMapping("/api/lib/statistics")
@RequiredArgsConstructor
public class LibDocumentStatisticsController {

    private final LibDocumentStatisticsService statisticsService;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<LibDashboardStatisticsResponse> getDashboardStatistics() {
        LibDashboardStatisticsResponse response = statisticsService.getDashboardStatistics();
        return Result.success(response);
    }
}
