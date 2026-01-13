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
public class WorkTeamDetailResponse {

    private Long id;

    private String teamName;

    private Long teamLeaderId;

    private String teamLeaderName;

    private Long caseId;

    private String caseName;

    private String teamDescription;

    private String status;

    private List<WorkTeamMemberResponse> members;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
