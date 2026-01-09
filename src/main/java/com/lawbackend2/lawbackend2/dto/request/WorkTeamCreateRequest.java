package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WorkTeamCreateRequest {

    @NotBlank(message = "团队名称不能为空")
    private String teamName;

    @NotNull(message = "团队负责人ID不能为空")
    private Long teamLeaderId;

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    private String teamDescription;
}
