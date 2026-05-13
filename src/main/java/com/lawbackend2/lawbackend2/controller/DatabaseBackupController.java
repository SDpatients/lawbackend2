package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.annotation.AuditLog;
import com.lawbackend2.lawbackend2.common.PageResult;
import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.config.BackupProperties;
import com.lawbackend2.lawbackend2.dto.request.BackupQueryRequest;
import com.lawbackend2.lawbackend2.dto.response.BackupStatisticsResponse;
import com.lawbackend2.lawbackend2.entity.BackupRecord;
import com.lawbackend2.lawbackend2.repository.BackupRecordRepository;
import com.lawbackend2.lawbackend2.service.DatabaseBackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/system/backup")
@RequiredArgsConstructor
@Tag(name = "数据库备份管理", description = "数据库备份相关接口")
public class DatabaseBackupController {

    private final DatabaseBackupService databaseBackupService;
    private final BackupRecordRepository backupRecordRepository;
    private final BackupProperties backupProperties;

    @GetMapping("/list")
    @Operation(summary = "查询备份记录列表", description = "分页查询备份记录列表，支持按状态、类型、时间范围筛选")
    @PreAuthorize("hasAuthority('system:backup:list')")
    public Result<PageResult<BackupRecord>> list(BackupQueryRequest queryRequest) {
        Page<BackupRecord> page = backupRecordRepository.findAllWithFilters(
                queryRequest.getStatus(),
                queryRequest.getBackupType(),
                queryRequest.getStartDate(),
                queryRequest.getEndDate(),
                org.springframework.data.domain.PageRequest.of(
                        queryRequest.getPage() - 1,
                        queryRequest.getSize()
                )
        );
        PageResult<BackupRecord> result = new PageResult<>();
        result.setList(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setPage(queryRequest.getPage());
        result.setSize(queryRequest.getSize());
        return Result.success(result);
    }

    @PostMapping("/execute")
    @Operation(summary = "手动执行备份", description = "手动触发一次数据库备份")
    @PreAuthorize("hasAuthority('system:backup:execute')")
    @AuditLog(module = "database-backup", moduleName = "数据库备份管理", operationType = "BACKUP", operationName = "手动执行备份")
    public Result<BackupRecord> executeBackup() {
        if (databaseBackupService.isBackupRunning()) {
            return Result.error("备份任务正在执行中，请稍后再试");
        }

        try {
            BackupRecord record = databaseBackupService.executeBackup(BackupRecord.TYPE_MANUAL);
            if (record != null) {
                return Result.success(record);
            } else {
                return Result.error("备份任务启动失败");
            }
        } catch (Exception e) {
            log.error("手动备份执行失败", e);
            return Result.error("备份执行失败: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(summary = "获取备份状态", description = "获取当前备份状态和最近备份信息")
    @PreAuthorize("hasAuthority('system:backup:list')")
    public Result<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("isRunning", databaseBackupService.isBackupRunning());

        Optional<BackupRecord> latestBackup = databaseBackupService.getLatestBackup();
        latestBackup.ifPresent(backup -> {
            Map<String, Object> latest = new HashMap<>();
            latest.put("id", backup.getId());
            latest.put("fileName", backup.getFileName());
            latest.put("status", backup.getStatus());
            latest.put("startTime", backup.getStartTime());
            latest.put("endTime", backup.getEndTime());
            latest.put("fileSize", backup.getFileSize());
            latest.put("backupType", backup.getBackupType());
            status.put("latestBackup", latest);
        });

        Optional<BackupRecord> latestSuccess = databaseBackupService.getLatestSuccessBackup();
        latestSuccess.ifPresent(backup -> {
            Map<String, Object> success = new HashMap<>();
            success.put("id", backup.getId());
            success.put("fileName", backup.getFileName());
            success.put("startTime", backup.getStartTime());
            success.put("fileSize", backup.getFileSize());
            status.put("latestSuccessBackup", success);
        });

        return Result.success(status);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "查询备份详情", description = "根据ID查询备份记录详情")
    @PreAuthorize("hasAuthority('system:backup:list')")
    public Result<BackupRecord> detail(
            @Parameter(description = "备份记录ID") @PathVariable Long id) {
        Optional<BackupRecord> record = backupRecordRepository.findById(id);
        return record.map(Result::success).orElse(Result.error("备份记录不存在"));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载备份文件", description = "下载指定的备份文件")
    @PreAuthorize("hasAuthority('system:backup:download')")
    public ResponseEntity<Resource> downloadBackup(
            @Parameter(description = "备份记录ID") @PathVariable Long id) {
        try {
            InputStream inputStream = databaseBackupService.getBackupFileStream(id);
            Optional<BackupRecord> optionalRecord = backupRecordRepository.findById(id);

            if (optionalRecord.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            BackupRecord record = optionalRecord.get();
            String fileName = record.getFileName();

            if (fileName.endsWith(".gz")) {
                fileName = fileName.substring(0, fileName.length() - 3);
            }

            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (FileNotFoundException e) {
            log.error("备份文件不存在: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("下载备份文件失败: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除备份记录", description = "删除指定的备份记录和文件")
    @PreAuthorize("hasAuthority('system:backup:delete')")
    @AuditLog(module = "database-backup", moduleName = "数据库备份管理", operationType = "DELETE", operationName = "删除备份记录")
    public Result<Void> deleteBackup(
            @Parameter(description = "备份记录ID") @PathVariable Long id) {
        boolean deleted = databaseBackupService.deleteBackup(id);
        if (deleted) {
            return Result.success(null);
        } else {
            return Result.error("删除备份失败");
        }
    }

    @PostMapping("/cleanup")
    @Operation(summary = "清理过期备份", description = "手动触发清理过期的备份文件")
    @PreAuthorize("hasAuthority('system:backup:execute')")
    public Result<Void> cleanupOldBackups() {
        try {
            databaseBackupService.cleanupOldBackups();
            return Result.success(null);
        } catch (Exception e) {
            log.error("清理过期备份失败", e);
            return Result.error("清理失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取备份统计信息", description = "获取备份的汇总统计数据")
    @PreAuthorize("hasAuthority('system:backup:list')")
    public Result<BackupStatisticsResponse> statistics() {
        long successCount = databaseBackupService.getSuccessCount();
        long failedCount = databaseBackupService.getFailedCount();
        long runningCount = databaseBackupService.getRunningCount();
        long totalFileSize = databaseBackupService.getTotalFileSize();

        Optional<BackupRecord> latestBackup = databaseBackupService.getLatestBackup();
        Optional<BackupRecord> latestSuccess = databaseBackupService.getLatestSuccessBackup();

        String totalFileSizeDisplay;
        if (totalFileSize < 1024) {
            totalFileSizeDisplay = totalFileSize + " B";
        } else if (totalFileSize < 1024 * 1024) {
            totalFileSizeDisplay = String.format("%.2f KB", totalFileSize / 1024.0);
        } else if (totalFileSize < 1024 * 1024 * 1024) {
            totalFileSizeDisplay = String.format("%.2f MB", totalFileSize / (1024.0 * 1024));
        } else {
            totalFileSizeDisplay = String.format("%.2f GB", totalFileSize / (1024.0 * 1024 * 1024));
        }

        BackupStatisticsResponse response = BackupStatisticsResponse.builder()
                .totalCount(successCount + failedCount + runningCount)
                .successCount(successCount)
                .failedCount(failedCount)
                .runningCount(runningCount)
                .totalFileSize(totalFileSize)
                .totalFileSizeDisplay(totalFileSizeDisplay)
                .lastBackupTime(latestBackup.map(BackupRecord::getStartTime).orElse(null))
                .lastSuccessBackupTime(latestSuccess.map(BackupRecord::getStartTime).orElse(null))
                .lastSuccessFileName(latestSuccess.map(BackupRecord::getFileName).orElse(null))
                .backupPath(backupProperties.getPath())
                .retentionDays(backupProperties.getRetentionDays())
                .backupEnabled(backupProperties.isEnabled())
                .cronExpression(backupProperties.getCron())
                .build();

        return Result.success(response);
    }
}
