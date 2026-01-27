package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CaseTaskSubmissionFileUpdateRequest {
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    private String description;
}
