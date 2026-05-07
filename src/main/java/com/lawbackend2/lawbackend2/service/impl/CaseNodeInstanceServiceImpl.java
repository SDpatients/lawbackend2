package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;
import com.lawbackend2.lawbackend2.entity.CaseNodeTemplate;
import com.lawbackend2.lawbackend2.enums.AlertLevel;
import com.lawbackend2.lawbackend2.enums.CalculationBase;
import com.lawbackend2.lawbackend2.enums.NodeStatus;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.repository.CaseNodeInstanceRepository;
import com.lawbackend2.lawbackend2.repository.CaseNodeTemplateRepository;
import com.lawbackend2.lawbackend2.service.CaseNodeAlertService;
import com.lawbackend2.lawbackend2.service.CaseNodeInstanceService;
import com.lawbackend2.lawbackend2.service.CaseNodeTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CaseNodeInstanceServiceImpl implements CaseNodeInstanceService {

    private final CaseNodeInstanceRepository caseNodeInstanceRepository;
    private final CaseNodeTemplateRepository caseNodeTemplateRepository;
    private final CaseNodeTemplateService caseNodeTemplateService;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final CaseNodeAlertService caseNodeAlertService;

    public CaseNodeInstanceServiceImpl(CaseNodeInstanceRepository caseNodeInstanceRepository,
                                       CaseNodeTemplateRepository caseNodeTemplateRepository,
                                       CaseNodeTemplateService caseNodeTemplateService,
                                       BankruptCaseRepository bankruptCaseRepository,
                                       CaseNodeAlertService caseNodeAlertService) {
        this.caseNodeInstanceRepository = caseNodeInstanceRepository;
        this.caseNodeTemplateRepository = caseNodeTemplateRepository;
        this.caseNodeTemplateService = caseNodeTemplateService;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.caseNodeAlertService = caseNodeAlertService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CaseNodeInstance> createNodesForCase(Long caseId, String caseType, LocalDate baseDate) {
        log.info("为案件创建节点实例, caseId: {}, caseType: {}, baseDate: {}", caseId, caseType, baseDate);

        BankruptCase bankruptCase = bankruptCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("案件不存在, caseId: " + caseId));

        List<CaseNodeTemplate> templates = caseNodeTemplateService.getTemplatesByCaseType(caseType);
        if (templates.isEmpty()) {
            log.warn("未找到案件类型对应的节点模板, caseType: {}", caseType);
            return new ArrayList<>();
        }

        List<CaseNodeInstance> instances = new ArrayList<>();
        Map<String, CaseNodeInstance> codeToInstanceMap = new HashMap<>();
        Map<String, LocalDate> codeToCompletionDateMap = new HashMap<>();

        for (CaseNodeTemplate template : templates) {
            CaseNodeInstance instance = new CaseNodeInstance();
            instance.setCaseId(caseId);
            instance.setTemplateId(template.getId());
            instance.setNodeCode(template.getNodeCode());
            instance.setNodeName(template.getNodeName());
            instance.setNodeStatus(NodeStatus.PENDING.name());
            instance.setSortOrder(template.getSortOrder());
            instance.setExtensionCount(0);
            instance.setExtensionDays(0);

            // 计算起算日期
            LocalDate startDate = calculateStartDate(template, baseDate, bankruptCase, codeToCompletionDateMap);
            instance.setStartDate(startDate);

            // 计算截止日期
            LocalDate deadlineDate = startDate.plusDays(template.getLegalDeadlineDays());
            instance.setDeadlineDate(deadlineDate);

            // 设置责任人(默认为案件主要负责人)
            instance.setResponsiblePersonName(bankruptCase.getMainResponsiblePerson());

            instances.add(instance);
            codeToInstanceMap.put(template.getNodeCode(), instance);
        }

        // 保存实例并建立前置节点关联
        List<CaseNodeInstance> savedInstances = caseNodeInstanceRepository.saveAll(instances);

        // 更新前置节点关联
        for (int i = 0; i < templates.size(); i++) {
            CaseNodeTemplate template = templates.get(i);
            CaseNodeInstance instance = savedInstances.get(i);

            if (template.getPrevNodeCode() != null && !template.getPrevNodeCode().isEmpty()) {
                CaseNodeInstance prevInstance = codeToInstanceMap.get(template.getPrevNodeCode());
                if (prevInstance != null) {
                    instance.setPrevNodeInstanceId(prevInstance.getId());
                }
            }
        }

        caseNodeInstanceRepository.saveAll(savedInstances);

        // 自动启动第一个节点(如果没有前置依赖)
        for (CaseNodeInstance instance : savedInstances) {
            if (instance.getPrevNodeInstanceId() == null && instance.getStartDate() != null
                    && !instance.getStartDate().isAfter(LocalDate.now())) {
                instance.setNodeStatus(NodeStatus.IN_PROGRESS.name());
            }
        }
        caseNodeInstanceRepository.saveAll(savedInstances);

        // 计算预警级别
        recalculateAlertLevels();

        log.info("成功为案件创建 {} 个节点实例, caseId: {}", savedInstances.size(), caseId);
        return savedInstances;
    }

    private LocalDate calculateStartDate(CaseNodeTemplate template, LocalDate baseDate,
                                          BankruptCase bankruptCase, Map<String, LocalDate> codeToCompletionDateMap) {
        String calculationBase = template.getCalculationBase();

        if (CalculationBase.ACCEPTANCE_DATE.name().equals(calculationBase)) {
            return bankruptCase.getAcceptanceDate() != null ? bankruptCase.getAcceptanceDate() : baseDate;
        } else if (CalculationBase.ANNOUNCEMENT_DATE.name().equals(calculationBase)) {
            // 公告日通常与受理日相同或稍后, 这里简化处理
            return bankruptCase.getAcceptanceDate() != null ? bankruptCase.getAcceptanceDate() : baseDate;
        } else if (CalculationBase.PREV_NODE_COMPLETE.name().equals(calculationBase)) {
            if (template.getPrevNodeCode() != null && codeToCompletionDateMap.containsKey(template.getPrevNodeCode())) {
                return codeToCompletionDateMap.get(template.getPrevNodeCode());
            }
            // 如果没有前置节点完成日期, 使用受理日
            return bankruptCase.getAcceptanceDate() != null ? bankruptCase.getAcceptanceDate() : baseDate;
        } else if (CalculationBase.REORGANIZATION_DATE.name().equals(calculationBase)) {
            // 裁定重整日, 这里简化使用受理日
            return bankruptCase.getAcceptanceDate() != null ? bankruptCase.getAcceptanceDate() : baseDate;
        } else if (CalculationBase.MEETING_PASS_DATE.name().equals(calculationBase)) {
            // 会议通过日, 简化处理
            return bankruptCase.getAcceptanceDate() != null ? bankruptCase.getAcceptanceDate().plusDays(45) : baseDate.plusDays(45);
        } else if (CalculationBase.COMPLETION_DATE.name().equals(calculationBase)) {
            // 完成日依赖前置节点
            if (template.getPrevNodeCode() != null && codeToCompletionDateMap.containsKey(template.getPrevNodeCode())) {
                return codeToCompletionDateMap.get(template.getPrevNodeCode());
            }
            return bankruptCase.getAcceptanceDate() != null ? bankruptCase.getAcceptanceDate() : baseDate;
        }

        return baseDate;
    }

    @Override
    public List<CaseNodeInstance> getNodesByCaseId(Long caseId) {
        return caseNodeInstanceRepository.findByCaseIdOrderBySortOrder(caseId);
    }

    @Override
    public List<CaseNodeInstance> getNodesByCaseIdAndStatus(Long caseId, String nodeStatus) {
        return caseNodeInstanceRepository.findByCaseIdAndNodeStatusOrderBySortOrder(caseId, nodeStatus);
    }

    @Override
    public CaseNodeInstance getNodeById(Long id) {
        return caseNodeInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("节点实例不存在, id: " + id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeInstance startNode(Long nodeInstanceId, Long userId) {
        log.info("启动节点, nodeInstanceId: {}, userId: {}", nodeInstanceId, userId);

        CaseNodeInstance node = getNodeById(nodeInstanceId);

        if (!NodeStatus.PENDING.name().equals(node.getNodeStatus())) {
            throw new RuntimeException("只有待启动状态的节点才能启动, 当前状态: " + node.getNodeStatus());
        }

        // 检查前置节点是否已完成
        if (node.getPrevNodeInstanceId() != null) {
            CaseNodeInstance prevNode = getNodeById(node.getPrevNodeInstanceId());
            if (!NodeStatus.COMPLETED.name().equals(prevNode.getNodeStatus())) {
                throw new RuntimeException("前置节点尚未完成, 无法启动当前节点");
            }
        }

        node.setNodeStatus(NodeStatus.IN_PROGRESS.name());
        node.setStartDate(LocalDate.now());
        node.setUpdateUserId(userId);

        CaseNodeInstance saved = caseNodeInstanceRepository.save(node);
        caseNodeAlertService.updateNodeAlertLevel(saved);

        log.info("节点启动成功, nodeInstanceId: {}", nodeInstanceId);
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeInstance completeNode(Long nodeInstanceId, String completionRemark, Long userId) {
        log.info("完成节点, nodeInstanceId: {}, userId: {}", nodeInstanceId, userId);

        CaseNodeInstance node = getNodeById(nodeInstanceId);

        if (!NodeStatus.IN_PROGRESS.name().equals(node.getNodeStatus())
                && !NodeStatus.EXTENDED.name().equals(node.getNodeStatus())) {
            throw new RuntimeException("只有进行中或已延期状态的节点才能完成, 当前状态: " + node.getNodeStatus());
        }

        node.setNodeStatus(NodeStatus.COMPLETED.name());
        node.setCompletedDate(LocalDate.now());
        node.setCompletionRemark(completionRemark);
        node.setUpdateUserId(userId);
        node.setAlertLevel(null);

        CaseNodeInstance saved = caseNodeInstanceRepository.save(node);

        // 自动启动后续节点
        activateNextNodes(saved);

        log.info("节点完成成功, nodeInstanceId: {}", nodeInstanceId);
        return saved;
    }

    private void activateNextNodes(CaseNodeInstance completedNode) {
        List<CaseNodeInstance> nextNodes = caseNodeInstanceRepository
                .findByCaseIdAndPrevNodeInstanceId(completedNode.getCaseId(), completedNode.getId());

        for (CaseNodeInstance nextNode : nextNodes) {
            if (NodeStatus.PENDING.name().equals(nextNode.getNodeStatus())) {
                nextNode.setNodeStatus(NodeStatus.IN_PROGRESS.name());
                nextNode.setStartDate(LocalDate.now());
                caseNodeInstanceRepository.save(nextNode);
                caseNodeAlertService.updateNodeAlertLevel(nextNode);
                log.info("自动启动后续节点, nodeInstanceId: {}, nodeName: {}", nextNode.getId(), nextNode.getNodeName());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeInstance updateNodeResponsiblePerson(Long nodeInstanceId, Long responsiblePersonId, String responsiblePersonName) {
        CaseNodeInstance node = getNodeById(nodeInstanceId);
        node.setResponsiblePersonId(responsiblePersonId);
        node.setResponsiblePersonName(responsiblePersonName);
        return caseNodeInstanceRepository.save(node);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNodesByCaseId(Long caseId) {
        log.info("删除案件的所有节点实例, caseId: {}", caseId);
        caseNodeInstanceRepository.deleteByCaseId(caseId);
    }

    @Override
    public void recalculateAlertLevels() {
        log.debug("重新计算所有进行中节点的预警级别");
        List<CaseNodeInstance> inProgressNodes = caseNodeInstanceRepository
                .findByCaseIdAndNodeStatusOrderBySortOrder(null, NodeStatus.IN_PROGRESS.name());

        for (CaseNodeInstance node : inProgressNodes) {
            caseNodeAlertService.updateNodeAlertLevel(node);
        }
    }

    @Override
    public long countByCaseIdAndStatus(Long caseId, String nodeStatus) {
        Long count = caseNodeInstanceRepository.countByCaseIdAndNodeStatus(caseId, nodeStatus);
        return count != null ? count : 0;
    }

    @Override
    public long countByCaseId(Long caseId) {
        Long count = caseNodeInstanceRepository.countByCaseId(caseId);
        return count != null ? count : 0;
    }
}
