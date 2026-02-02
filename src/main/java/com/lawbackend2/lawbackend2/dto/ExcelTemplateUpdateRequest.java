package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class ExcelTemplateUpdateRequest {
    
    private String templateName;
    
    private String description;
    
    @NotNull(message = "字段映射不能为空")
    private Map<String, String> fieldMappings;
}
