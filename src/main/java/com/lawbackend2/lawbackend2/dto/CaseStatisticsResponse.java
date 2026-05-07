package com.lawbackend2.lawbackend2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "案件统计数据")
public class CaseStatisticsResponse {

    @Schema(description = "所有案件数量")
    private Long totalCases;

    @Schema(description = "待处理案件数量")
    private Long pendingCases;

    @Schema(description = "进行中案件数量")
    private Long ongoingCases;

    @Schema(description = "报结中案件数量")
    private Long awaitingCases;

    @Schema(description = "已结案案件数量")
    private Long completedCases;

    @Schema(description = "已归档案件数量")
    private Long archivedCases;

    private Map<String, Long> statusDistribution;

    private Map<String, Long> progressDistribution;

    private Long simplifiedTrialCases;

    private Long normalTrialCases;

    private BigDecimal averageReviewCount;

    private Long todayCreatedCases;

    private Long monthCreatedCases;

    private Long yearCreatedCases;
}