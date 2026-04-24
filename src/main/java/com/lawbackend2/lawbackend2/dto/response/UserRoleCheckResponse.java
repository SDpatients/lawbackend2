package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleCheckResponse {

    private Long userId;

    private String username;

    private Boolean isAdmin;

    private Boolean isSuperAdmin;

    private List<String> roles;

    public boolean hasRole(String roleCode) {
        return roles != null && roles.contains(roleCode);
    }
}
