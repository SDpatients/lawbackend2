package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

@Data
public class CaseTaskUpdateRequest {
    private String taskDescription;
    private String status;
}
