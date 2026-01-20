package com.lawbackend2.lawbackend2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class WorkTeamMemberPermissionRequest {

    @NotBlank(message = "权限级别不能为空")
    @Pattern(regexp = "^(VIEW|EDIT|ADMIN|管理|查看)$", message = "权限级别不正确")
    @JsonProperty("permission_level")
    private String permissionLevel;

    @Pattern(regexp = "^(负责人|成员)$", message = "团队角色不正确")
    @JsonProperty("team_role")
    private String teamRole;
}
