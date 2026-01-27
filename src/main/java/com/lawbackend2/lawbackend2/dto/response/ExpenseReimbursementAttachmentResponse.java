package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseReimbursementAttachmentResponse {

    private Long id;
    private Long reimbursementId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private LocalDateTime uploadTime;
    private Integer sortOrder;
}
