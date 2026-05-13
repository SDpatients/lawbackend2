package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackupStatisticsResponse {

    private long totalCount;

    private long successCount;

    private long failedCount;

    private long runningCount;

    private long totalFileSize;

    private String totalFileSizeDisplay;

    private LocalDateTime lastBackupTime;

    private LocalDateTime lastSuccessBackupTime;

    private String lastSuccessFileName;

    private String backupPath;

    private int retentionDays;

    private boolean backupEnabled;

    private String cronExpression;
}