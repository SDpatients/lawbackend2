package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.CaseNodeAlertRecord;
import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;
import com.lawbackend2.lawbackend2.enums.AlertLevel;
import com.lawbackend2.lawbackend2.enums.NodeStatus;
import com.lawbackend2.lawbackend2.enums.NotificationType;
import com.lawbackend2.lawbackend2.repository.CaseNodeAlertRecordRepository;
import com.lawbackend2.lawbackend2.repository.CaseNodeInstanceRepository;
import com.lawbackend2.lawbackend2.service.CaseNodeAlertService;
import com.lawbackend2.lawbackend2.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CaseNodeAlertServiceImpl implements CaseNodeAlertService {

    private final CaseNodeInstanceRepository caseNodeInstanceRepository;
    private final CaseNodeAlertRecordRepository caseNodeAlertRecordRepository;
    private final NotificationService notificationService;

    public CaseNodeAlertServiceImpl(CaseNodeInstanceRepository caseNodeInstanceRepository,
                                    CaseNodeAlertRecordRepository caseNodeAlertRecordRepository,
                                    NotificationService notificationService) {
        this.caseNodeInstanceRepository = caseNodeInstanceRepository;
        this.caseNodeAlertRecordRepository = caseNodeAlertRecordRepository;
        this.notificationService = notificationService;
    }

    @Override
    public String calculateAlertLevel(CaseNodeInstance nodeInstance) {
        if (nodeInstance == null || nodeInstance.getDeadlineDate() == null) {
            return null;
        }

        if (!NodeStatus.IN_PROGRESS.name().equals(nodeInstance.getNodeStatus())
                && !NodeStatus.EXTENDED.name().equals(nodeInstance.getNodeStatus())) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate deadline = nodeInstance.getDeadlineDate();
        long remainingDays = ChronoUnit.DAYS.between(today, deadline);

        if (remainingDays < 0) {
            return AlertLevel.OVERDUE.name();
        } else if (remainingDays == 0) {
            return AlertLevel.DUE_TODAY.name();
        } else if (remainingDays <= 3) {
            return AlertLevel.SOON_DUE.name();
        } else {
            return AlertLevel.NORMAL.name();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNodeAlertLevel(CaseNodeInstance nodeInstance) {
        String newAlertLevel = calculateAlertLevel(nodeInstance);
        String oldAlertLevel = nodeInstance.getAlertLevel();

        if (newAlertLevel != null && !newAlertLevel.equals(oldAlertLevel)) {
            nodeInstance.setAlertLevel(newAlertLevel);
            nodeInstance.setAlertTriggeredAt(java.time.LocalDateTime.now());
            caseNodeInstanceRepository.save(nodeInstance);

            // 生成预警记录
            generateAlertRecord(nodeInstance);

            log.info("节点预警级别更新, nodeInstanceId: {}, nodeName: {}, oldLevel: {}, newLevel: {}",
                    nodeInstance.getId(), nodeInstance.getNodeName(), oldAlertLevel, newAlertLevel);
        } else if (newAlertLevel == null) {
            nodeInstance.setAlertLevel(null);
            nodeInstance.setAlertTriggeredAt(null);
            caseNodeInstanceRepository.save(nodeInstance);
        }
    }

    private void generateAlertRecord(CaseNodeInstance nodeInstance) {
        LocalDate today = LocalDate.now();
        long remainingDays = ChronoUnit.DAYS.between(today, nodeInstance.getDeadlineDate());

        // 检查今天是否已生成过相同级别的预警记录
        var existing = caseNodeAlertRecordRepository
                .findByNodeInstanceIdAndAlertLevelAndAlertDate(nodeInstance.getId(), nodeInstance.getAlertLevel(), today);

        if (existing.isPresent()) {
            return;
        }

        CaseNodeAlertRecord record = new CaseNodeAlertRecord();
        record.setNodeInstanceId(nodeInstance.getId());
        record.setCaseId(nodeInstance.getCaseId());
        record.setAlertLevel(nodeInstance.getAlertLevel());
        record.setRemainingDays((int) remainingDays);
        record.setDeadlineDate(nodeInstance.getDeadlineDate());
        record.setAlertDate(today);
        record.setIsNotified(false);
        record.setRecipientId(nodeInstance.getResponsiblePersonId());
        record.setRecipientName(nodeInstance.getResponsiblePersonName());

        caseNodeAlertRecordRepository.save(record);
        log.debug("生成预警记录, nodeInstanceId: {}, alertLevel: {}", nodeInstance.getId(), nodeInstance.getAlertLevel());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanAndGenerateAlerts() {
        log.info("开始扫描节点预警");
        LocalDate today = LocalDate.now();

        // 扫描所有进行中的节点并更新预警级别
        List<CaseNodeInstance> inProgressNodes = caseNodeInstanceRepository
                .findByCaseIdAndNodeStatusOrderBySortOrder(null, NodeStatus.IN_PROGRESS.name());

        int alertCount = 0;
        for (CaseNodeInstance node : inProgressNodes) {
            String oldLevel = node.getAlertLevel();
            updateNodeAlertLevel(node);
            if (node.getAlertLevel() != null && !node.getAlertLevel().equals(AlertLevel.NORMAL.name())
                    && !node.getAlertLevel().equals(oldLevel)) {
                alertCount++;
            }
        }

        log.info("节点预警扫描完成, 新增预警: {}", alertCount);
    }

    @Override
    public List<CaseNodeAlertRecord> getAlertRecordsByCaseId(Long caseId) {
        return caseNodeAlertRecordRepository.findByCaseIdOrderByAlertDateDesc(caseId);
    }

    @Override
    public List<CaseNodeAlertRecord> getUnnotifiedAlerts() {
        return caseNodeAlertRecordRepository.findUnnotifiedRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsNotified(Long alertRecordId, String notificationType) {
        CaseNodeAlertRecord record = caseNodeAlertRecordRepository.findById(alertRecordId)
                .orElseThrow(() -> new RuntimeException("预警记录不存在, id: " + alertRecordId));

        record.setIsNotified(true);
        record.setNotificationType(notificationType);
        record.setNotificationTime(java.time.LocalDateTime.now());
        caseNodeAlertRecordRepository.save(record);
    }

    @Override
    public List<CaseNodeInstance> getAlertDashboard(Long userId) {
        List<CaseNodeInstance> result = new ArrayList<>();

        // 已逾期
        result.addAll(getOverdueNodes());
        // 今日到期
        result.addAll(getDueTodayNodes());
        // 即将到期
        result.addAll(getSoonDueNodes());

        // 如果指定了用户ID, 过滤该用户负责的节点
        if (userId != null) {
            result.removeIf(node -> !userId.equals(node.getResponsiblePersonId()));
        }

        return result;
    }

    @Override
    public List<CaseNodeInstance> getOverdueNodes() {
        return caseNodeInstanceRepository.findOverdueNodes(LocalDate.now());
    }

    @Override
    public List<CaseNodeInstance> getDueTodayNodes() {
        return caseNodeInstanceRepository.findInProgressByDeadlineDate(LocalDate.now());
    }

    @Override
    public List<CaseNodeInstance> getSoonDueNodes() {
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        return caseNodeInstanceRepository.findInProgressByDeadlineBefore(threeDaysLater);
    }
}
