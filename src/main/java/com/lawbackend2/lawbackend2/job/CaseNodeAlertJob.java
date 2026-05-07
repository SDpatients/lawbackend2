package com.lawbackend2.lawbackend2.job;

import com.lawbackend2.lawbackend2.entity.CaseNodeAlertRecord;
import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;
import com.lawbackend2.lawbackend2.enums.AlertLevel;
import com.lawbackend2.lawbackend2.enums.NotificationType;
import com.lawbackend2.lawbackend2.service.CaseNodeAlertService;
import com.lawbackend2.lawbackend2.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class CaseNodeAlertJob {

    private final CaseNodeAlertService caseNodeAlertService;
    private final NotificationService notificationService;

    public CaseNodeAlertJob(CaseNodeAlertService caseNodeAlertService,
                            NotificationService notificationService) {
        this.caseNodeAlertService = caseNodeAlertService;
        this.notificationService = notificationService;
    }

    /**
     * 每日凌晨2点扫描节点预警
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void dailyAlertScan() {
        log.info("开始执行每日节点预警扫描任务, time: {}", java.time.LocalDateTime.now());
        caseNodeAlertService.scanAndGenerateAlerts();
        log.info("每日节点预警扫描任务完成");
    }

    /**
     * 每日上午9点发送预警通知
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void sendAlertNotifications() {
        log.info("开始发送节点预警通知, time: {}", java.time.LocalDateTime.now());

        List<CaseNodeAlertRecord> unnotifiedAlerts = caseNodeAlertService.getUnnotifiedAlerts();
        int sentCount = 0;

        for (CaseNodeAlertRecord record : unnotifiedAlerts) {
            try {
                sendNotification(record);
                caseNodeAlertService.markAsNotified(record.getId(), NotificationType.SYSTEM_MSG.name());
                sentCount++;
            } catch (Exception e) {
                log.error("发送预警通知失败, alertRecordId: {}", record.getId(), e);
            }
        }

        log.info("节点预警通知发送完成, 共发送: {}", sentCount);
    }

    private void sendNotification(CaseNodeAlertRecord record) {
        String title = buildNotificationTitle(record);
        String content = buildNotificationContent(record);

        Long recipientId = record.getRecipientId();
        if (recipientId != null) {
            notificationService.sendNotificationToUser(
                    recipientId,
                    title,
                    content,
                    "CASE_NODE_ALERT",
                    record.getNodeInstanceId(),
                    "CASE_NODE_INSTANCE",
                    null,
                    "系统"
            );
        }
    }

    private String buildNotificationTitle(CaseNodeAlertRecord record) {
        String alertLevelDesc;
        String level = record.getAlertLevel();
        if ("OVERDUE".equals(level)) {
            alertLevelDesc = "已逾期";
        } else if ("DUE_TODAY".equals(level)) {
            alertLevelDesc = "今日到期";
        } else if ("SOON_DUE".equals(level)) {
            alertLevelDesc = "即将到期";
        } else {
            alertLevelDesc = "预警提醒";
        }
        return "【" + alertLevelDesc + "】破产案件节点预警";
    }

    private String buildNotificationContent(CaseNodeAlertRecord record) {
        long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), record.getDeadlineDate());
        String daysDesc;
        if (remainingDays < 0) {
            daysDesc = "已逾期 " + Math.abs(remainingDays) + " 天";
        } else if (remainingDays == 0) {
            daysDesc = "今日到期";
        } else {
            daysDesc = "还剩 " + remainingDays + " 天";
        }

        return "案件节点「" + record.getNodeInstanceId() + "」" + daysDesc
                + ", 截止日期: " + record.getDeadlineDate()
                + ", 请及时处理。";
    }
}
