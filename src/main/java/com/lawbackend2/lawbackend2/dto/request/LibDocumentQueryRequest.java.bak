package com.lawbackend2.lawbackend2.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibDocumentQueryRequest {

    private Long folderId;

    private String documentType;

    private String status;

    private String keyword;

    private Boolean isPublic;

    private Long createUserId;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private String sortBy = "createTime";

    @Builder.Default
    private String sortOrder = "desc";
}
