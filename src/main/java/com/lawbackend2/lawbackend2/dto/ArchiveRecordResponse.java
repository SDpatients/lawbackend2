package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArchiveRecordResponse {
    private Long id;

    private Long caseId;

    private String categoryCode;

    private String categoryName;

    private Long fileId;

    private String archiveNo;

    private String fileTitle;

    private String fileDescription;

    private Long uploadUserId;

    private String uploadUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;

    private String status;

    private Boolean isConfidential;

    private String accessLevel;

    private Integer version;

    private Long parentVersionId;

    private FileRecordInfo file;
}
