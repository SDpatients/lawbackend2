package com.lawbackend2.lawbackend2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "临时上传文件响应")
public class TempUploadFileResponse {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "原始文件名")
    private String originalFileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件扩展名")
    private String fileExtension;

    @Schema(description = "MIME类型")
    private String mimeType;

    @Schema(description = "文件描述")
    private String description;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "Token")
    private String token;
}
