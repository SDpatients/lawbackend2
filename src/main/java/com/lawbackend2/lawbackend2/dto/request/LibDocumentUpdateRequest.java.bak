package com.lawbackend2.lawbackend2.dto.request;

import javax.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibDocumentUpdateRequest {

    @Size(max = 255, message = "文档名称最长255个字符")
    private String documentName;

    private Long folderId;

    private String description;

    private String tags;

    private Boolean isPublic;

    private String status;
}
