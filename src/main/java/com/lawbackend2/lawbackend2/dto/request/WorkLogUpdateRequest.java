package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class WorkLogUpdateRequest {

    private Long caseId;

    private LocalDate workDate;

    @NotBlank(message = "工作类型不能为空")
    private String workType;

    @NotBlank(message = "工作内容不能为空")
    private String workContent;

    private String workResult;

    private String attachmentIds;

    private String remark;
}
