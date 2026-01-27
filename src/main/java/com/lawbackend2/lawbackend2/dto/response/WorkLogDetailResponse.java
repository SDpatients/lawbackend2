package com.lawbackend2.lawbackend2.dto.response;

import com.lawbackend2.lawbackend2.entity.FileRecord;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkLogDetailResponse {

    private Long id;

    private Long caseId;

    private LocalDate workDate;

    private String workType;

    private String workContent;

    private String workResult;

    private String attachmentIds;

    private List<FileRecord> attachments;

    private String remark;

    private String status;

    private Long createUserId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
