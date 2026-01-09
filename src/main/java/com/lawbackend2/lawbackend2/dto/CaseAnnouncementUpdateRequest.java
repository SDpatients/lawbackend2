package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CaseAnnouncementUpdateRequest {

    private String title;

    private String content;

    private String announcementType;

    private String attachments;
}
