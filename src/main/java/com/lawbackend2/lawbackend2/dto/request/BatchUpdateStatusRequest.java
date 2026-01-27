package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchUpdateStatusRequest {
    private List<Long> taskIds;
    private String status;
}
