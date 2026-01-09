package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WorkTeamUpdateRequest {
    @NotNull(message = "团队ID不能为空")
    private Long teamId;

    private String teamName;

    private String teamDescription;

    private String status;
}
