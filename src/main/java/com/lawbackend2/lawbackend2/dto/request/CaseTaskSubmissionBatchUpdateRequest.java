package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CaseTaskSubmissionBatchUpdateRequest {
    @NotNull(message = "提交ID不能为空")
    private Long id;

    private String submissionTitle;

    private String submissionContent;

    private String submissionType;

    private List<FileOperation> fileOperations;

    @Data
    public static class FileOperation {
        private Long fileId;

        private String operation;

        private String description;
    }
}
