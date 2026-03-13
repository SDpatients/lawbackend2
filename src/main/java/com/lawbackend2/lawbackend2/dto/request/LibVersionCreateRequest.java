package com.lawbackend2.lawbackend2.dto.request;

import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibVersionCreateRequest {

    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    @NotNull(message = "文件名不能为空")
    private String fileName;

    @NotNull(message = "文件路径不能为空")
    private String filePath;

    private Long fileSize;

    private String changeSummary;

    @Builder.Default
    private Boolean isMajor = false;
}
