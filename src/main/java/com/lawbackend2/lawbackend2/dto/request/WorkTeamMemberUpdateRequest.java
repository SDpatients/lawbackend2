package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class WorkTeamMemberUpdateRequest {

    @Pattern(regexp = "^(VIEW|EDIT|ADMIN|管理|查看)$", message = "权限级别不正确")
    private String permissionLevel;

    private String teamRole;
}
