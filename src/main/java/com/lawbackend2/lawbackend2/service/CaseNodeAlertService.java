package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.CaseNodeAlertRecord;
import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;

import java.util.List;

public interface CaseNodeAlertService {

    String calculateAlertLevel(CaseNodeInstance nodeInstance);

    void updateNodeAlertLevel(CaseNodeInstance nodeInstance);

    void scanAndGenerateAlerts();

    List<CaseNodeAlertRecord> getAlertRecordsByCaseId(Long caseId);

    List<CaseNodeAlertRecord> getUnnotifiedAlerts();

    void markAsNotified(Long alertRecordId, String notificationType);

    List<CaseNodeInstance> getAlertDashboard(Long userId);

    List<CaseNodeInstance> getOverdueNodes();

    List<CaseNodeInstance> getDueTodayNodes();

    List<CaseNodeInstance> getSoonDueNodes();
}
