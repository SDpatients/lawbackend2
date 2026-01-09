package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class WorkTeamMemberPermissionRequest {

    @NotBlank(message = "权限级别不能为空")
    @Pattern(regexp = "^(VIEW|EDIT|ADMIN)$", message = "权限级别不正确")
    private String permissionLevel;

    @NotBlank(message = "权限类型不能为空")
    private String permissionType;
}
