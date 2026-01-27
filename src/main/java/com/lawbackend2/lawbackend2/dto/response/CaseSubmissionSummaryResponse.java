package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CaseSubmissionSummaryResponse {
    private Long caseId;
    private String caseNumber;
    private List<StageSubmissionSummary> stages;

    @Data
    public static class StageSubmissionSummary {
        private Integer stageNum;
        private String stageName;
        private List<TaskSubmissionSummary> tasks;
    }

    @Data
    public static class TaskSubmissionSummary {
        private Long taskId;
        private String taskCode;
        private String taskName;
        private String taskDescription;
        private String status;
        private List<SubmissionInfo> submissions;
    }

    @Data
    public static class SubmissionInfo {
        private Long submissionId;
        private String submissionTitle;
        private String submissionContent;
        private String submissionType;
        private Integer submissionNumber;
        private String status;
        private String creatorName;
        private Long reviewerId;
        private String reviewOpinion;
        private String reviewTime;
        private String createTime;
        private String updateTime;
        private Integer fileCount;
    }
}
