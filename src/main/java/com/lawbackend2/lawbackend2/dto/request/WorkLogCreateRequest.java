package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class WorkLogCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotNull(message = "工作日期不能为空")
    private LocalDate workDate;

    @NotBlank(message = "工作类型不能为空")
    private String workType;

    @NotBlank(message = "工作内容不能为空")
    private String workContent;

    private String workResult;

    private String attachmentIds;

    private String remark;
}
