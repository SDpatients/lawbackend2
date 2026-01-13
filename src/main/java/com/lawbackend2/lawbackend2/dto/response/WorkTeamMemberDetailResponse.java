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
public class WorkTeamMemberDetailResponse {

    private Long id;

    private Long teamId;

    private String teamName;

    private Long caseId;

    private String caseName;

    private Long userId;

    private String userName;

    private String userRealName;

    private String teamRole;

    private String permissionLevel;

    private Integer isActive;

    private String status;

    private List<WorkTeamPermissionResponse> permissions;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
