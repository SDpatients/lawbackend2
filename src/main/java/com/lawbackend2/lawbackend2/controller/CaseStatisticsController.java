package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CaseStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CaseStatisticsResponse;
import com.lawbackend2.lawbackend2.service.CaseStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/case-statistics")
@Tag(name = "案件统计", description = "案件统计相关接口")
public class CaseStatisticsController {

    @Autowired
    private CaseStatisticsService caseStatisticsService;

    @GetMapping
    @Operation(summary = "获取案件统计数据", description = "根据条件获取案件统计数据，支持按日期范围、法院、状态等条件筛选")
    public Result<CaseStatisticsResponse> getCaseStatistics(
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate,
            @Parameter(description = "法院ID") @RequestParam(required = false) Long courtId,
            @Parameter(description = "案件状态") @RequestParam(required = false) String caseStatus,
            @Parameter(description = "案件进度") @RequestParam(required = false) String caseProgress) {

        log.info("获取案件统计数据，startDate：{}，endDate：{}，courtId：{}，caseStatus：{}，caseProgress：{}",
                startDate, endDate, courtId, caseStatus, caseProgress);

        CaseStatisticsRequest request = new CaseStatisticsRequest();

        try {
            if (startDate != null) {
                request.setStartDate(java.time.LocalDate.parse(startDate));
            }
            if (endDate != null) {
                request.setEndDate(java.time.LocalDate.parse(endDate));
            }
            request.setCourtId(courtId);
            request.setCaseStatus(caseStatus);
            request.setCaseProgress(caseProgress);

            CaseStatisticsResponse response = caseStatisticsService.getCaseStatistics(request);

            log.info("案件统计数据获取成功");
            return Result.success(response);

        } catch (Exception e) {
            log.error("获取案件统计数据失败：{}", e.getMessage(), e);
            return Result.error("获取案件统计数据失败：" + e.getMessage());
        }
    }
}
