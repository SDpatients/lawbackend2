package com.lawbackend2.lawbackend2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建临时上传Token请求")
public class TempUploadTokenCreateRequest {

    @Schema(description = "业务类型", required = true, example = "DOCUMENT")
    private String bizType;

    @Schema(description = "Token描述", example = "文书上传")
    private String description;

    @Schema(description = "过期时间(分钟),默认30分钟", example = "30")
    private Integer expireMinutes;
}
