package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AnnouncementViewRecordCreateRequest {
    @NotNull(message = "公告ID不能为空")
    private Long announcementId;

    private String announcementTitle;

    private Long caseId;

    private String caseName;

    private Long viewerId;

    private String viewerName;

    private String viewerType;

    private String ipAddress;

    private String userAgent;

    private Integer viewDuration;

    private String deviceType;

    private String browserType;

    private String osType;

    private String location;
}
