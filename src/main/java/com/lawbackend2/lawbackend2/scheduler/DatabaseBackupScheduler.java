package com.lawbackend2.lawbackend2.scheduler;

import com.lawbackend2.lawbackend2.config.BackupProperties;
import com.lawbackend2.lawbackend2.entity.BackupRecord;
import com.lawbackend2.lawbackend2.service.DatabaseBackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "backup", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DatabaseBackupScheduler {

    private final DatabaseBackupService databaseBackupService;
    private final BackupProperties backupProperties;

    @Scheduled(cron = "${backup.cron:0 0 2 * * ?}")
    public void scheduledBackup() {
        log.info("定时备份任务触发，执行时间表达式: {}", backupProperties.getCron());

        if (databaseBackupService.isBackupRunning()) {
            log.warn("已有备份任务正在执行，跳过本次定时备份");
            return;
        }

        try {
            BackupRecord record = databaseBackupService.executeBackup(BackupRecord.TYPE_FULL);
            if (record != null) {
                log.info("定时备份任务完成，状态: {}, 文件: {}",
                        record.getStatus(), record.getFileName());
            }
        } catch (Exception e) {
            log.error("定时备份任务执行失败", e);
        }
    }
}
