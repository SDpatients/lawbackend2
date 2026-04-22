package com.lawbackend2.lawbackend2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "我的待办统计数据")
public class MyTodoStatisticsResponse {

    @Schema(description = "进行中待办数量（未完成的）")
    private Long inProgressTodos;

    @Schema(description = "已完成待办数量")
    private Long completedTodos;

    @Schema(description = "已逾期数量")
    private Long overdueTodos;
}
