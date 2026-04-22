package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lawbackend2.lawbackend2.config.FlexibleLocalDateTimeDeserializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewAttachmentDto {
    private Object file;

    private String id;

    private String originalFileName;

    private Long fileSize;

    private String fileExtension;

    private String mimeType;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime uploadTime;

    private Boolean isExisting;

    private String filePath;

    private Boolean isMobileFile;

    private Long tempFileId;
}
