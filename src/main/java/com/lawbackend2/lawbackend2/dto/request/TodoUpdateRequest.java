package com.lawbackend2.lawbackend2.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "待办事项更新请求")
public class TodoUpdateRequest {

    @Schema(description = "待办标题")
    private String title;

    @Schema(description = "待办描述")
    private String description;

    @Schema(description = "待办类型")
    private String type;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "关联ID")
    private Long relatedId;

    @Schema(description = "关联类型")
    private String relatedType;

    @Schema(description = "案号（用于自动关联案件）")
    private String caseNumber;

    @Schema(description = "案件ID（用于自动关联案件）")
    private Long caseId;
}
