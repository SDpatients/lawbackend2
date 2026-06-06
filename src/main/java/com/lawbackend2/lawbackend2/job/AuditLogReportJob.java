package com.lawbackend2.lawbackend2.job;

import com.lawbackend2.lawbackend2.service.EmailService;
import com.lawbackend2.lawbackend2.service.impl.AuditReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogReportJob {

    private final AuditReportService auditReportService;
    private final EmailService emailService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    @Scheduled(cron = "${audit.report.weekly-cron:0 0 18 ? * FRI}")
    public void generateAndSendWeeklyReport() {
        log.info("开始生成并发送审计日志周报...");

        try {
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusDays(7);

            String htmlReport = auditReportService.generateWeeklyReportHtml(startTime, endTime);
            String textReport = auditReportService.generateWeeklyReportText(startTime, endTime);

            String subject = String.format("🛡️ 审计日志周报（%s 至 %s）",
                    startTime.format(DATE_FORMATTER),
                    endTime.format(DATE_FORMATTER));

            emailService.sendAuditReportToManagers(subject, htmlReport);

            log.info("审计日志周报生成并发送成功！周期：{} 至 {}",
                    startTime.format(DATE_FORMATTER),
                    endTime.format(DATE_FORMATTER));

        } catch (Exception e) {
            log.error("生成或发送审计日志周报失败", e);
        }
    }

    @Scheduled(cron = "${audit.report.daily-cron:0 0 1 ? * *}")
    public void verifyDailyIntegrity() {
        log.info("开始每日审计日志完整性校验...");

        try {
            var integrityReport = auditReportService.generateWeeklyReportSummary(
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now());

            Boolean hasIssue = (Boolean) integrityReport.get("hasIntegrityIssue");
            if (hasIssue != null && hasIssue) {
                Long tamperedCount = (Long) integrityReport.get("tamperedCount");
                log.warn("每日完整性校验发现 {} 条审计日志可能存在完整性问题！", tamperedCount);

                String subject = "⚠️ 审计日志完整性警告";
                String content = String.format(
                        "系统检测到 %d 条审计日志可能存在完整性问题，请立即检查。\n\n" +
                        "此为系统自动发送的警告邮件，请勿回复。",
                        tamperedCount);
                emailService.sendAuditReportToManagers(subject, content);
            } else {
                log.info("每日完整性校验通过，未发现问题。");
            }

        } catch (Exception e) {
            log.error("每日完整性校验失败", e);
        }
    }

    public void sendReportManually(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("手动发送审计日志报告：{} 至 {}", startTime, endTime);

        try {
            String htmlReport = auditReportService.generateWeeklyReportHtml(startTime, endTime);

            String subject = String.format("🛡️ 审计日志报告（%s 至 %s）[手动发送]",
                    startTime.format(DATE_FORMATTER),
                    endTime.format(DATE_FORMATTER));

            emailService.sendAuditReportToManagers(subject, htmlReport);

            log.info("手动发送审计日志报告成功！");

        } catch (Exception e) {
            log.error("手动发送审计日志报告失败", e);
        }
    }
}
