package com.lawbackend2.lawbackend2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "临时上传Token响应")
public class TempUploadTokenResponse {

    @Schema(description = "Token ID")
    private Long id;

    @Schema(description = "Token字符串")
    private String token;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "文件数量")
    private Integer fileCount;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "二维码内容(用于生成二维码)")
    private String qrCodeContent;
}
