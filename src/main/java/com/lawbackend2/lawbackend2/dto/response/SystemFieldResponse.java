package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

@Data
public class SystemFieldResponse {
    private String label;
    private String value;
    private Integer sortOrder;
    private String description;
}
