package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class ExcelTemplateCreateRequest {
    
    @NotBlank(message = "模板名称不能为空")
    private String templateName;
    
    @NotBlank(message = "模板编码不能为空")
    private String templateCode;
    
    private String description;
    
    @NotNull(message = "字段映射不能为空")
    private Map<String, String> fieldMappings;
}
