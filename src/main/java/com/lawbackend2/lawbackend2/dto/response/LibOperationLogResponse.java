package com.lawbackend2.lawbackend2.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibOperationLogResponse {

    private Long id;
    private Long documentId;
    private String documentName;
    private Long folderId;
    private String folderName;
    private String operationType;
    private String operationDetail;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createTime;
    private Long createUserId;
    private String createUserName;
}
