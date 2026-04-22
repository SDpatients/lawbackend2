package com.lawbackend2.lawbackend2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "我的案件统计数据")
public class MyCaseStatisticsResponse {

    @Schema(description = "所有案件数量")
    private Long totalCases;

    @Schema(description = "进行中案件数量")
    private Long inProgressCases;

    @Schema(description = "已结案数量")
    private Long completedCases;
}
