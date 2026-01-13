package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithRolesResponse {

    private Long id;

    private String username;

    private String realName;

    private String mobile;

    private String email;

    private String phone;

    private Character isValid;

    private String status;

    private Character loginType;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private Integer loginCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<RoleInfo> roles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private Long id;
        private String roleCode;
        private String roleName;
        private String roleDesc;
        private Character isSystem;
        private String status;
        private Integer sortOrder;
    }
}
