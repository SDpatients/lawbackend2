package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ExcelTemplateImportRequest {

    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    private Long sheetIndex;
}
