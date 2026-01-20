package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class WorkTeamMemberCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotNull(message = "用户ID列表不能为空")
    private List<Long> userId;

    @NotBlank(message = "团队角色不能为空")
    private String teamRole;

    @Pattern(regexp = "^(VIEW|EDIT|ADMIN|管理|查看)$", message = "权限级别不正确")
    private String permissionLevel;
}
