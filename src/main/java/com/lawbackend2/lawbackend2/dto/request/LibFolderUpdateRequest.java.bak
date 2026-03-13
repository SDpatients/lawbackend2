package com.lawbackend2.lawbackend2.dto.request;

import javax.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibFolderUpdateRequest {

    @Size(max = 100, message = "文件夹名称最长100个字符")
    private String folderName;

    private String description;

    private String icon;

    private String color;

    private Boolean isPublic;

    private Integer sortOrder;

    private Long parentId;
}
