package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.CaseNodeExtension;
import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;
import com.lawbackend2.lawbackend2.enums.ExtensionApprovalStatus;
import com.lawbackend2.lawbackend2.enums.NodeStatus;
import com.lawbackend2.lawbackend2.repository.CaseNodeExtensionRepository;
import com.lawbackend2.lawbackend2.repository.CaseNodeInstanceRepository;
import com.lawbackend2.lawbackend2.service.CaseNodeAlertService;
import com.lawbackend2.lawbackend2.service.CaseNodeExtensionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CaseNodeExtensionServiceImpl implements CaseNodeExtensionService {

    private final CaseNodeExtensionRepository caseNodeExtensionRepository;
    private final CaseNodeInstanceRepository caseNodeInstanceRepository;
    private final CaseNodeAlertService caseNodeAlertService;

    public CaseNodeExtensionServiceImpl(CaseNodeExtensionRepository caseNodeExtensionRepository,
                                        CaseNodeInstanceRepository caseNodeInstanceRepository,
                                        CaseNodeAlertService caseNodeAlertService) {
        this.caseNodeExtensionRepository = caseNodeExtensionRepository;
        this.caseNodeInstanceRepository = caseNodeInstanceRepository;
        this.caseNodeAlertService = caseNodeAlertService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeExtension applyExtension(Long nodeInstanceId, Long caseId, Integer extensionDays,
                                             String applyReason, Long applyUserId, String applyUserName) {
        log.info("申请节点延期, nodeInstanceId: {}, caseId: {}, extensionDays: {}",
                nodeInstanceId, caseId, extensionDays);

        if (extensionDays == null || extensionDays <= 0) {
            throw new RuntimeException("延期天数必须大于0");
        }

        CaseNodeInstance node = caseNodeInstanceRepository.findById(nodeInstanceId)
                .orElseThrow(() -> new RuntimeException("节点实例不存在, id: " + nodeInstanceId));

        if (!NodeStatus.IN_PROGRESS.name().equals(node.getNodeStatus())
                && !NodeStatus.EXTENDED.name().equals(node.getNodeStatus())) {
            throw new RuntimeException("只有进行中或已延期状态的节点才能申请延期");
        }

        // 检查是否已有待审批的延期申请
        List<CaseNodeExtension> existingApplications = caseNodeExtensionRepository
                .findByNodeInstanceIdOrderByApplyTimeDesc(nodeInstanceId);
        for (CaseNodeExtension ext : existingApplications) {
            if (ExtensionApprovalStatus.PENDING.name().equals(ext.getApprovalStatus())) {
                throw new RuntimeException("该节点已有待审批的延期申请, 请勿重复申请");
            }
        }

        LocalDate originalDeadline = node.getDeadlineDate();
        LocalDate newDeadline = originalDeadline.plusDays(extensionDays);

        CaseNodeExtension extension = new CaseNodeExtension();
        extension.setNodeInstanceId(nodeInstanceId);
        extension.setCaseId(caseId);
        extension.setExtensionDays(extensionDays);
        extension.setOriginalDeadline(originalDeadline);
        extension.setNewDeadline(newDeadline);
        extension.setApplyReason(applyReason);
        extension.setApplyUserId(applyUserId);
        extension.setApplyUserName(applyUserName);
        extension.setApplyTime(LocalDateTime.now());
        extension.setApprovalStatus(ExtensionApprovalStatus.PENDING.name());

        CaseNodeExtension saved = caseNodeExtensionRepository.save(extension);

        // 更新节点状态为延期申请中
        node.setNodeStatus(NodeStatus.EXTENSION_REQUESTED.name());
        caseNodeInstanceRepository.save(node);

        log.info("节点延期申请提交成功, extensionId: {}, nodeInstanceId: {}", saved.getId(), nodeInstanceId);
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeExtension approveExtension(Long extensionId, Long approverId, String approverName, String approvalOpinion) {
        log.info("审批通过节点延期申请, extensionId: {}, approverId: {}", extensionId, approverId);

        CaseNodeExtension extension = caseNodeExtensionRepository.findByIdAndNotDeleted(extensionId)
                .orElseThrow(() -> new RuntimeException("延期申请不存在, id: " + extensionId));

        if (!ExtensionApprovalStatus.PENDING.name().equals(extension.getApprovalStatus())) {
            throw new RuntimeException("该延期申请不处于待审批状态, 当前状态: " + extension.getApprovalStatus());
        }

        // 更新延期申请状态
        extension.setApprovalStatus(ExtensionApprovalStatus.APPROVED.name());
        extension.setApproverId(approverId);
        extension.setApproverName(approverName);
        extension.setApprovalTime(LocalDateTime.now());
        extension.setApprovalOpinion(approvalOpinion);
        CaseNodeExtension saved = caseNodeExtensionRepository.save(extension);

        // 更新节点实例
        CaseNodeInstance node = caseNodeInstanceRepository.findById(extension.getNodeInstanceId())
                .orElseThrow(() -> new RuntimeException("节点实例不存在"));

        node.setDeadlineDate(extension.getNewDeadline());
        node.setExtensionCount(node.getExtensionCount() + 1);
        node.setExtensionDays(node.getExtensionDays() + extension.getExtensionDays());
        node.setNodeStatus(NodeStatus.EXTENDED.name());
        caseNodeInstanceRepository.save(node);

        // 重新计算预警级别
        caseNodeAlertService.updateNodeAlertLevel(node);

        log.info("节点延期申请审批通过, extensionId: {}, nodeInstanceId: {}, newDeadline: {}",
                extensionId, node.getId(), extension.getNewDeadline());
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CaseNodeExtension rejectExtension(Long extensionId, Long approverId, String approverName, String approvalOpinion) {
        log.info("驳回节点延期申请, extensionId: {}, approverId: {}", extensionId, approverId);

        CaseNodeExtension extension = caseNodeExtensionRepository.findByIdAndNotDeleted(extensionId)
                .orElseThrow(() -> new RuntimeException("延期申请不存在, id: " + extensionId));

        if (!ExtensionApprovalStatus.PENDING.name().equals(extension.getApprovalStatus())) {
            throw new RuntimeException("该延期申请不处于待审批状态, 当前状态: " + extension.getApprovalStatus());
        }

        // 更新延期申请状态
        extension.setApprovalStatus(ExtensionApprovalStatus.REJECTED.name());
        extension.setApproverId(approverId);
        extension.setApproverName(approverName);
        extension.setApprovalTime(LocalDateTime.now());
        extension.setApprovalOpinion(approvalOpinion);
        CaseNodeExtension saved = caseNodeExtensionRepository.save(extension);

        // 恢复节点状态为进行中
        CaseNodeInstance node = caseNodeInstanceRepository.findById(extension.getNodeInstanceId())
                .orElseThrow(() -> new RuntimeException("节点实例不存在"));

        node.setNodeStatus(NodeStatus.IN_PROGRESS.name());
        caseNodeInstanceRepository.save(node);

        log.info("节点延期申请已驳回, extensionId: {}, nodeInstanceId: {}", extensionId, node.getId());
        return saved;
    }

    @Override
    public List<CaseNodeExtension> getExtensionsByNodeInstanceId(Long nodeInstanceId) {
        return caseNodeExtensionRepository.findByNodeInstanceIdOrderByApplyTimeDesc(nodeInstanceId);
    }

    @Override
    public List<CaseNodeExtension> getExtensionsByCaseId(Long caseId) {
        return caseNodeExtensionRepository.findByCaseIdOrderByApplyTimeDesc(caseId);
    }

    @Override
    public List<CaseNodeExtension> getPendingApprovals() {
        return caseNodeExtensionRepository.findPendingApprovals();
    }

    @Override
    public List<CaseNodeExtension> getPendingApprovalsByApproverId(Long approverId) {
        return caseNodeExtensionRepository.findPendingByApproverId(approverId);
    }

    @Override
    public CaseNodeExtension getExtensionById(Long id) {
        return caseNodeExtensionRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("延期申请不存在, id: " + id));
    }
}
