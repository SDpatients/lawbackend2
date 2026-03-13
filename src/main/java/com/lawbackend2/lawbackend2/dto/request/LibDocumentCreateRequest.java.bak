package com.lawbackend2.lawbackend2.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibDocumentCreateRequest {

    @NotBlank(message = "文档名称不能为空")
    @Size(max = 255, message = "文档名称最长255个字符")
    private String documentName;

    private String documentCode;

    private Long folderId;

    @NotBlank(message = "文档类型不能为空")
    private String documentType;

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    private Long fileSize;

    private String fileExtension;

    private String mimeType;

    private String description;

    private String tags;

    @Builder.Default
    private Boolean isPublic = false;
}
