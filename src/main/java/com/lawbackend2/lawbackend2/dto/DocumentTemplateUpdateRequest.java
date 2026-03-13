package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DocumentTemplateUpdateRequest {

    @NotNull(message = "模板ID不能为空")
    private Long id;

    private String templateName;

    private String description;

    private String filePath;

    private String configJson;

    private Boolean isDefault;

    private String status;

    private List<DocumentTemplateFieldDTO> fields;

    private List<DocumentTemplateMappingDTO> mappings;
}
