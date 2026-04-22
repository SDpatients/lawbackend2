package com.lawbackend2.lawbackend2.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "待办事项创建请求")
public class TodoCreateRequest {

    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "待办标题", required = true)
    private String title;

    @Schema(description = "待办描述")
    private String description;

    @Schema(description = "待办类型")
    private String type;

    @Schema(description = "优先级", example = "NORMAL")
    private String priority;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "被分配人ID")
    private Long assigneeId;

    @Schema(description = "被分配人姓名")
    private String assigneeName;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "创建人姓名")
    private String createUserName;

    @Schema(description = "关联ID（如果未提供关联类型，则通过案号或案件ID自动关联）")
    private Long relatedId;

    @Schema(description = "关联类型")
    private String relatedType;

    @Schema(description = "案号（用于自动关联案件）")
    private String caseNumber;

    @Schema(description = "案件ID（用于自动关联案件）")
    private Long caseId;
}
