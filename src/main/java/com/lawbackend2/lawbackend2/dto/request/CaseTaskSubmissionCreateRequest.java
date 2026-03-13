package com.lawbackend2.lawbackend2.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CaseTaskSubmissionCreateRequest {
    @NotNull(message = "任务 ID 不能为空")
    private Long caseTaskId;

    @NotBlank(message = "提交标题不能为空")
    private String submissionTitle;

    private String submissionContent;

    private String submissionType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
}
