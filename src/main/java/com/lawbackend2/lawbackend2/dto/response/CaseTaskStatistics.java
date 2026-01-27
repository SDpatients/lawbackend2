package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

@Data
public class CaseTaskStatistics {
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer inProgressTasks;
    private Integer reviewingTasks;
    private Integer skippedTasks;
    private Integer rejectedTasks;
    private Double completionRate;
    private Integer totalFiles;
}
