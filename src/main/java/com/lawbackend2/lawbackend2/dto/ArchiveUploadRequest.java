package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ArchiveUploadRequest {
    @NotBlank(message = "归档分类代码不能为空")
    private String categoryCode;

    private String fileTitle;

    private String fileDescription;

    private Boolean isConfidential = false;

    private String accessLevel = "INTERNAL";
}
