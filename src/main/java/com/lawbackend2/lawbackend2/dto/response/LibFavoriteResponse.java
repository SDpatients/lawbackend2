package com.lawbackend2.lawbackend2.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibFavoriteResponse {

    private Long id;
    private Long documentId;
    private String documentName;
    private String documentType;
    private String fileName;
    private Long fileSize;
    private String folderName;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime documentCreateTime;
}
