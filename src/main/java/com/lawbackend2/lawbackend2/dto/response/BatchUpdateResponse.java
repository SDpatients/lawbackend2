package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

@Data
public class BatchUpdateResponse {
    private Integer successCount;
    private Integer failCount;
}
