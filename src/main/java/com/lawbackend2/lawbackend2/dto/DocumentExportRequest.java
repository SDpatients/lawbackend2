package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class DocumentExportRequest {

    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    private Map<String, Object> data;

    private String fileName;
}
