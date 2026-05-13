package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.config.BackupProperties;
import com.lawbackend2.lawbackend2.entity.BackupRecord;
import com.lawbackend2.lawbackend2.repository.BackupRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseBackupService {

    private final BackupProperties backupProperties;
    private final BackupRecordRepository backupRecordRepository;
    private final TransactionTemplate transactionTemplate;

    private final AtomicBoolean isBackupRunning = new AtomicBoolean(false);

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public BackupRecord executeBackup(String backupType) {
        if (!isBackupRunning.compareAndSet(false, true)) {
            log.warn("备份任务正在执行中，跳过本次请求");
            return null;
        }

        BackupRecord record = BackupRecord.builder()
                .backupType(backupType != null ? backupType : BackupRecord.TYPE_FULL)
                .status(BackupRecord.STATUS_PENDING)
                .databaseName(backupProperties.getDatabase())
                .createTime(LocalDateTime.now())
                .build();

        try {
            record = saveRunningRecord(record);

            log.info("开始执行数据库备份，类型: {}, 数据库: {}", record.getBackupType(), backupProperties.getDatabase());

            Path backupDir = Paths.get(backupProperties.getPath());
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
                log.info("创建备份目录: {}", backupDir.toAbsolutePath());
            }

            String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
            String baseFileName = String.format("%s_backup_%s", backupProperties.getDatabase(), timestamp);
            String sqlFileName = baseFileName + ".sql";
            Path sqlFilePath = backupDir.resolve(sqlFileName);

            ProcessBuilder processBuilder = buildMysqldumpCommand(sqlFilePath);
            log.debug("执行备份命令: {}", String.join(" ", processBuilder.command()));

            Process process = processBuilder.start();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorBuilder = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorBuilder.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMsg = errorBuilder.toString();
                log.error("mysqldump 执行失败，退出码: {}, 错误: {}", exitCode, errorMsg);
                record = saveFailedRecord(record, "mysqldump 执行失败: " + errorMsg, record.getStartTime());
                return record;
            }

            long fileSize = Files.size(sqlFilePath);
            log.info("SQL 文件生成成功: {}, 大小: {} bytes", sqlFilePath, fileSize);

            Path finalFilePath = sqlFilePath;
            String finalFileName = sqlFileName;

            if (backupProperties.isCompress()) {
                finalFileName = baseFileName + ".sql.gz";
                Path compressedPath = backupDir.resolve(finalFileName);
                compressFile(sqlFilePath, compressedPath);
                Files.deleteIfExists(sqlFilePath);
                finalFilePath = compressedPath;
                fileSize = Files.size(compressedPath);
                log.info("文件压缩完成: {}, 压缩后大小: {} bytes", compressedPath, fileSize);
            }

            record = saveSuccessRecord(record, finalFilePath, finalFileName, fileSize);

            log.info("数据库备份完成，耗时: {} ms", record.getDuration());

            cleanupOldBackups();

        } catch (Exception e) {
            log.error("数据库备份失败", e);
            record = saveFailedRecord(record, e.getMessage(), record.getStartTime());
        } finally {
            isBackupRunning.set(false);
        }

        return record;
    }

    private BackupRecord saveRunningRecord(BackupRecord record) {
        String placeholder = "backup_pending";
        record.setFileName(placeholder);
        record.setFilePath(placeholder);
        record.setStatus(BackupRecord.STATUS_RUNNING);
        record.setStartTime(LocalDateTime.now());
        return transactionTemplate.execute(status -> backupRecordRepository.save(record));
    }

    private BackupRecord saveSuccessRecord(BackupRecord record, Path filePath, String fileName, long fileSize) {
        return transactionTemplate.execute(status -> {
            record.setFileName(fileName);
            record.setFilePath(filePath.toAbsolutePath().toString());
            record.setFileSize(fileSize);
            record.setStatus(BackupRecord.STATUS_SUCCESS);
            record.setEndTime(LocalDateTime.now());
            record.setDuration(Duration.between(record.getStartTime(), record.getEndTime()).toMillis());
            return backupRecordRepository.save(record);
        });
    }

    private BackupRecord saveFailedRecord(BackupRecord record, String errorMessage, LocalDateTime startTime) {
        return transactionTemplate.execute(status -> {
            record.setStatus(BackupRecord.STATUS_FAILED);
            record.setErrorMessage(errorMessage);
            record.setEndTime(LocalDateTime.now());
            if (startTime != null) {
                record.setDuration(Duration.between(startTime, record.getEndTime()).toMillis());
            }
            return backupRecordRepository.save(record);
        });
    }

    private ProcessBuilder buildMysqldumpCommand(Path outputPath) {
        ProcessBuilder pb = new ProcessBuilder(
                backupProperties.getMysqldumpPath(),
                "-h", backupProperties.getHost(),
                "-P", backupProperties.getPort(),
                "-u", backupProperties.getUsername(),
                "-p" + backupProperties.getPassword(),
                "--single-transaction",
                "--routines",
                "--triggers",
                "--events",
                "--set-gtid-purged=OFF",
                "--default-character-set=utf8mb4",
                "--quick",
                "--lock-tables=false",
                backupProperties.getDatabase()
        );

        pb.redirectOutput(outputPath.toFile());
        pb.redirectErrorStream(false);

        return pb;
    }

    private void compressFile(Path source, Path target) throws IOException {
        try (InputStream fis = Files.newInputStream(source);
             GZIPOutputStream gzos = new GZIPOutputStream(Files.newOutputStream(target))) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzos.write(buffer, 0, len);
            }
        }
    }

    @Transactional
    public void cleanupOldBackups() {
        int retentionDays = backupProperties.getRetentionDays();
        if (retentionDays <= 0) {
            log.debug("保留天数设置为 {}，跳过清理", retentionDays);
            return;
        }

        LocalDateTime threshold = LocalDateTime.now().minusDays(retentionDays);
        List<BackupRecord> oldRecords = backupRecordRepository.findByCreateTimeBeforeAndNotDeleted(threshold);

        log.info("开始清理 {} 天前的备份记录，共 {} 条", retentionDays, oldRecords.size());

        for (BackupRecord record : oldRecords) {
            try {
                Path filePath = Paths.get(record.getFilePath());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.debug("删除备份文件: {}", filePath);
                }
                record.setIsDeleted(true);
                backupRecordRepository.save(record);
            } catch (Exception e) {
                log.error("删除备份文件失败: {}", record.getFilePath(), e);
            }
        }
    }

    public boolean isBackupRunning() {
        return isBackupRunning.get();
    }

    public Optional<BackupRecord> getLatestBackup() {
        return backupRecordRepository.findFirstByOrderByStartTimeDesc();
    }

    public Optional<BackupRecord> getLatestSuccessBackup() {
        return backupRecordRepository.findFirstByStatusOrderByStartTimeDesc(BackupRecord.STATUS_SUCCESS);
    }

    @Transactional
    public boolean deleteBackup(Long id) {
        Optional<BackupRecord> optionalRecord = backupRecordRepository.findById(id);
        if (optionalRecord.isEmpty()) {
            return false;
        }

        BackupRecord record = optionalRecord.get();
        try {
            Path filePath = Paths.get(record.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("删除备份文件: {}", filePath);
            }
            record.setIsDeleted(true);
            backupRecordRepository.save(record);
            return true;
        } catch (Exception e) {
            log.error("删除备份失败: {}", id, e);
            return false;
        }
    }

    public InputStream getBackupFileStream(Long id) throws IOException {
        Optional<BackupRecord> optionalRecord = backupRecordRepository.findById(id);
        if (optionalRecord.isEmpty()) {
            throw new FileNotFoundException("备份记录不存在: " + id);
        }

        BackupRecord record = optionalRecord.get();
        Path filePath = Paths.get(record.getFilePath());

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("备份文件不存在: " + filePath);
        }

        if (record.getFileName().endsWith(".gz")) {
            return new GZIPInputStream(Files.newInputStream(filePath));
        }

        return Files.newInputStream(filePath);
    }

    public boolean isCompressed(Long id) {
        Optional<BackupRecord> optionalRecord = backupRecordRepository.findById(id);
        return optionalRecord.map(r -> r.getFileName().endsWith(".gz")).orElse(false);
    }

    public long getSuccessCount() {
        return backupRecordRepository.countByStatusAndNotDeleted(BackupRecord.STATUS_SUCCESS);
    }

    public long getFailedCount() {
        return backupRecordRepository.countByStatusAndNotDeleted(BackupRecord.STATUS_FAILED);
    }

    public long getRunningCount() {
        return backupRecordRepository.countByStatusAndNotDeleted(BackupRecord.STATUS_RUNNING);
    }

    public long getTotalCount() {
        return backupRecordRepository.countAllNotDeleted();
    }

    public long getTotalFileSize() {
        return backupRecordRepository.sumSuccessFileSize();
    }
}
