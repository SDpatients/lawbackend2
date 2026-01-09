package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsResponse;
import com.lawbackend2.lawbackend2.service.CreditorClaimStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/creditor-claim-statistics")
@Tag(name = "债权申报统计", description = "债权申报统计相关接口")
public class CreditorClaimStatisticsController {

    @Autowired
    private CreditorClaimStatisticsService creditorClaimStatisticsService;

    @GetMapping
    @Operation(summary = "获取债权申报统计数据", description = "根据条件获取债权申报统计数据，支持按日期范围、案件ID、状态等条件筛选")
    public Result<CreditorClaimStatisticsResponse> getCreditorClaimStatistics(
            @Parameter(description = "案件ID") @RequestParam(required = false) Long caseId,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate,
            @Parameter(description = "登记状态") @RequestParam(required = false) String registrationStatus,
            @Parameter(description = "债权类型") @RequestParam(required = false) String claimType,
            @Parameter(description = "债权性质") @RequestParam(required = false) String claimNature) {

        log.info("获取债权申报统计数据，caseId：{}，startDate：{}，endDate：{}，registrationStatus：{}，claimType：{}，claimNature：{}",
                caseId, startDate, endDate, registrationStatus, claimType, claimNature);

        CreditorClaimStatisticsRequest request = new CreditorClaimStatisticsRequest();

        try {
            request.setCaseId(caseId);
            if (startDate != null) {
                request.setStartDate(java.time.LocalDate.parse(startDate));
            }
            if (endDate != null) {
                request.setEndDate(java.time.LocalDate.parse(endDate));
            }
            request.setRegistrationStatus(registrationStatus);
            request.setClaimType(claimType);
            request.setClaimNature(claimNature);

            CreditorClaimStatisticsResponse response = creditorClaimStatisticsService.getCreditorClaimStatistics(request);

            log.info("债权申报统计数据获取成功");
            return Result.success(response);

        } catch (Exception e) {
            log.error("获取债权申报统计数据失败：{}", e.getMessage(), e);
            return Result.error("获取债权申报统计数据失败：" + e.getMessage());
        }
    }
}
