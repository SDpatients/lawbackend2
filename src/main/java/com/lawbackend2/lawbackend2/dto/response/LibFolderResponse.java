package com.lawbackend2.lawbackend2.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibFolderResponse {

    private Long id;
    private String folderName;
    private String folderPath;
    private Long parentId;
    private Integer folderLevel;
    private Integer sortOrder;
    private String description;
    private String icon;
    private String color;
    private Boolean isPublic;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private String createUserName;

    private Long documentCount;
    private Long subFolderCount;
    private List<LibFolderResponse> children;

    private Boolean isLocked;
    private Long lockedBy;
    private String lockedByName;
    private LocalDateTime lockedTime;
}
