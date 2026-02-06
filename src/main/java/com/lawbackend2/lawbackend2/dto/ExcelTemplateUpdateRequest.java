package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ExcelTemplateUpdateRequest {
    
    private String templateName;
    
    private String description;
    
    private Map<String, String> fieldMappings;
    
    private Boolean isActive;
}
