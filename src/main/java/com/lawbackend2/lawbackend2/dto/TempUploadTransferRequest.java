package com.lawbackend2.lawbackend2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "转移临时文件到业务请求")
public class TempUploadTransferRequest {

    @Schema(description = "临时上传Token", required = true)
    private String token;

    @Schema(description = "业务类型", required = true, example = "DOCUMENT")
    private String bizType;

    @Schema(description = "业务ID", required = true)
    private String bizId;
}
