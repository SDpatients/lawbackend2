package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CaseTaskResponse {
    private Long id;
    private Long caseId;
    private String taskCode;
    private String taskName;
    private String taskDescription;
    private String status;
    private Integer fileCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
