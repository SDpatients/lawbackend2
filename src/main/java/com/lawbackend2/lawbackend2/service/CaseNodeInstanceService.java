package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;

import java.time.LocalDate;
import java.util.List;

public interface CaseNodeInstanceService {

    List<CaseNodeInstance> createNodesForCase(Long caseId, String caseType, LocalDate baseDate);

    List<CaseNodeInstance> getNodesByCaseId(Long caseId);

    List<CaseNodeInstance> getNodesByCaseIdAndStatus(Long caseId, String nodeStatus);

    CaseNodeInstance getNodeById(Long id);

    CaseNodeInstance startNode(Long nodeInstanceId, Long userId);

    CaseNodeInstance completeNode(Long nodeInstanceId, String completionRemark, Long userId);

    CaseNodeInstance updateNodeResponsiblePerson(Long nodeInstanceId, Long responsiblePersonId, String responsiblePersonName);

    void deleteNodesByCaseId(Long caseId);

    void recalculateAlertLevels();

    long countByCaseIdAndStatus(Long caseId, String nodeStatus);

    long countByCaseId(Long caseId);
}
