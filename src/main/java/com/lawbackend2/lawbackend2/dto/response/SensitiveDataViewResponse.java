package com.lawbackend2.lawbackend2.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "敏感数据完整查看响应")
public class SensitiveDataViewResponse {

    @Schema(description = "明文值")
    private String plainTextValue;
}