package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ExcelTemplateExportRequest {

    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    private Long caseId;

    private String registrationStatus;
}
