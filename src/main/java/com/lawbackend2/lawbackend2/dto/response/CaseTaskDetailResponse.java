package com.lawbackend2.lawbackend2.dto.response;

import com.lawbackend2.lawbackend2.dto.FileRecordInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CaseTaskDetailResponse {
    private Long id;
    private Long caseId;
    private String caseNumber;
    private String taskCode;
    private String taskName;
    private String taskDescription;
    private String status;
    private List<FileRecordInfo> files;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
