package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DocumentTemplateCreateRequest {

    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @NotBlank(message = "模板类型不能为空")
    private String templateType;

    private String description;

    private String configJson;

    private Boolean isDefault = false;

    private List<DocumentTemplateFieldDTO> fields;
}
