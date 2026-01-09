package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CaseAnnouncementCreateRequest {

    @NotBlank(message = "案件ID不能为空")
    private Long caseId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "公告类型不能为空")
    private String announcementType;

    private String attachments;
}
