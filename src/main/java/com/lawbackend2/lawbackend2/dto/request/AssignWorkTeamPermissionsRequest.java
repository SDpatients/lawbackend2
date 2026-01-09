package com.lawbackend2.lawbackend2.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignWorkTeamPermissionsRequest {

    @NotEmpty(message = "权限列表不能为空")
    @Valid
    private List<WorkTeamPermissionItem> permissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkTeamPermissionItem {
        
        private String moduleType;
        
        private String permissionType;
        
        private Integer isAllowed;
    }
}
