package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class ArchiveUpdateRequest {
    private String fileTitle;

    private String fileDescription;

    private Boolean isConfidential;

    private String accessLevel;
}
