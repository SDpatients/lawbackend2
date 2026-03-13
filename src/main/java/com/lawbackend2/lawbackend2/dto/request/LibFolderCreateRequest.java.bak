package com.lawbackend2.lawbackend2.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibFolderCreateRequest {

    @NotBlank(message = "文件夹名称不能为空")
    @Size(max = 100, message = "文件夹名称最长100个字符")
    private String folderName;

    private Long parentId;

    private String description;

    private String icon;

    private String color;

    @Builder.Default
    private Boolean isPublic = false;

    @Builder.Default
    private Integer sortOrder = 0;
}
