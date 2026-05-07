package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.entity.CaseNodeAlertRecord;
import com.lawbackend2.lawbackend2.entity.CaseNodeExtension;
import com.lawbackend2.lawbackend2.entity.CaseNodeInstance;
import com.lawbackend2.lawbackend2.entity.CaseNodeTemplate;
import com.lawbackend2.lawbackend2.service.CaseNodeAlertService;
import com.lawbackend2.lawbackend2.service.CaseNodeExtensionService;
import com.lawbackend2.lawbackend2.service.CaseNodeInstanceService;
import com.lawbackend2.lawbackend2.service.CaseNodeTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "案件节点管理")
@RestController
@RequestMapping("/case-nodes")
@Validated
public class CaseNodeController {

    private final CaseNodeTemplateService caseNodeTemplateService;
    private final CaseNodeInstanceService caseNodeInstanceService;
    private final CaseNodeAlertService caseNodeAlertService;
    private final CaseNodeExtensionService caseNodeExtensionService;

    public CaseNodeController(CaseNodeTemplateService caseNodeTemplateService,
                              CaseNodeInstanceService caseNodeInstanceService,
                              CaseNodeAlertService caseNodeAlertService,
                              CaseNodeExtensionService caseNodeExtensionService) {
        this.caseNodeTemplateService = caseNodeTemplateService;
        this.caseNodeInstanceService = caseNodeInstanceService;
        this.caseNodeAlertService = caseNodeAlertService;
        this.caseNodeExtensionService = caseNodeExtensionService;
    }

    // ==================== 节点模板接口 ====================

    @Operation(summary = "查询所有节点模板")
    @GetMapping("/templates")
    public Result<List<CaseNodeTemplate>> getAllTemplates() {
        return Result.success(caseNodeTemplateService.getAllActiveTemplates());
    }

    @Operation(summary = "按案件类型查询节点模板")
    @GetMapping("/templates/{caseType}")
    public Result<List<CaseNodeTemplate>> getTemplatesByCaseType(
            @Parameter(description = "案件类型") @PathVariable String caseType) {
        return Result.success(caseNodeTemplateService.getTemplatesByCaseType(caseType));
    }

    // ==================== 节点实例接口 ====================

    @Operation(summary = "查询案件的节点实例列表")
    @GetMapping("/instances/{caseId}")
    public Result<List<CaseNodeInstance>> getNodesByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        return Result.success(caseNodeInstanceService.getNodesByCaseId(caseId));
    }

    @Operation(summary = "查询节点实例详情")
    @GetMapping("/instances/detail/{nodeInstanceId}")
    public Result<CaseNodeInstance> getNodeById(
            @Parameter(description = "节点实例ID") @PathVariable Long nodeInstanceId) {
        return Result.success(caseNodeInstanceService.getNodeById(nodeInstanceId));
    }

    @Operation(summary = "启动节点")
    @PostMapping("/instances/{nodeInstanceId}/start")
    public Result<CaseNodeInstance> startNode(
            @Parameter(description = "节点实例ID") @PathVariable Long nodeInstanceId) {
        Long userId = getCurrentUserId();
        return Result.success(caseNodeInstanceService.startNode(nodeInstanceId, userId));
    }

    @Operation(summary = "完成节点")
    @PostMapping("/instances/{nodeInstanceId}/complete")
    public Result<CaseNodeInstance> completeNode(
            @Parameter(description = "节点实例ID") @PathVariable Long nodeInstanceId,
            @Parameter(description = "完成备注") @RequestParam(required = false) String completionRemark) {
        Long userId = getCurrentUserId();
        return Result.success(caseNodeInstanceService.completeNode(nodeInstanceId, completionRemark, userId));
    }

    @Operation(summary = "更新节点责任人")
    @PatchMapping("/instances/{nodeInstanceId}/responsible-person")
    public Result<CaseNodeInstance> updateResponsiblePerson(
            @Parameter(description = "节点实例ID") @PathVariable Long nodeInstanceId,
            @Parameter(description = "责任人ID") @RequestParam Long responsiblePersonId,
            @Parameter(description = "责任人姓名") @RequestParam String responsiblePersonName) {
        return Result.success(caseNodeInstanceService.updateNodeResponsiblePerson(nodeInstanceId, responsiblePersonId, responsiblePersonName));
    }

    @Operation(summary = "查询案件节点统计")
    @GetMapping("/instances/statistics/{caseId}")
    public Result<Map<String, Long>> getNodeStatistics(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        long total = caseNodeInstanceService.countByCaseId(caseId);
        long completed = caseNodeInstanceService.countByCaseIdAndStatus(caseId, "COMPLETED");
        long inProgress = caseNodeInstanceService.countByCaseIdAndStatus(caseId, "IN_PROGRESS");
        long pending = caseNodeInstanceService.countByCaseIdAndStatus(caseId, "PENDING");

        return Result.success(Map.of(
                "total", total,
                "completed", completed,
                "inProgress", inProgress,
                "pending", pending
        ));
    }

    // ==================== 预警接口 ====================

    @Operation(summary = "查询预警看板")
    @GetMapping("/alerts/dashboard")
    public Result<List<CaseNodeInstance>> getAlertDashboard(
            @Parameter(description = "用户ID(不传则返回所有)") @RequestParam(required = false) Long userId) {
        return Result.success(caseNodeAlertService.getAlertDashboard(userId));
    }

    @Operation(summary = "查询已逾期节点")
    @GetMapping("/alerts/overdue")
    public Result<List<CaseNodeInstance>> getOverdueNodes() {
        return Result.success(caseNodeAlertService.getOverdueNodes());
    }

    @Operation(summary = "查询今日到期节点")
    @GetMapping("/alerts/due-today")
    public Result<List<CaseNodeInstance>> getDueTodayNodes() {
        return Result.success(caseNodeAlertService.getDueTodayNodes());
    }

    @Operation(summary = "查询即将到期节点")
    @GetMapping("/alerts/soon-due")
    public Result<List<CaseNodeInstance>> getSoonDueNodes() {
        return Result.success(caseNodeAlertService.getSoonDueNodes());
    }

    @Operation(summary = "查询案件预警记录")
    @GetMapping("/alerts/records/{caseId}")
    public Result<List<CaseNodeAlertRecord>> getAlertRecordsByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        return Result.success(caseNodeAlertService.getAlertRecordsByCaseId(caseId));
    }

    // ==================== 延期申请接口 ====================

    @Operation(summary = "提交延期申请")
    @PostMapping("/extensions/apply")
    public Result<CaseNodeExtension> applyExtension(
            @Parameter(description = "节点实例ID") @RequestParam Long nodeInstanceId,
            @Parameter(description = "案件ID") @RequestParam Long caseId,
            @Parameter(description = "延期天数") @RequestParam Integer extensionDays,
            @Parameter(description = "申请理由") @RequestParam String applyReason) {
        Long currentUserId = getCurrentUserId();
        return Result.success(caseNodeExtensionService.applyExtension(nodeInstanceId, caseId, extensionDays, applyReason, currentUserId, getCurrentUserName()));
    }

    @Operation(summary = "审批通过延期申请")
    @PostMapping("/extensions/{extensionId}/approve")
    public Result<CaseNodeExtension> approveExtension(
            @Parameter(description = "延期申请ID") @PathVariable Long extensionId,
            @Parameter(description = "审批意见") @RequestParam(required = false) String approvalOpinion) {
        Long currentUserId = getCurrentUserId();
        return Result.success(caseNodeExtensionService.approveExtension(extensionId, currentUserId, getCurrentUserName(), approvalOpinion));
    }

    @Operation(summary = "驳回延期申请")
    @PostMapping("/extensions/{extensionId}/reject")
    public Result<CaseNodeExtension> rejectExtension(
            @Parameter(description = "延期申请ID") @PathVariable Long extensionId,
            @Parameter(description = "审批意见") @RequestParam(required = false) String approvalOpinion) {
        Long currentUserId = getCurrentUserId();
        return Result.success(caseNodeExtensionService.rejectExtension(extensionId, currentUserId, getCurrentUserName(), approvalOpinion));
    }

    @Operation(summary = "查询节点的延期申请记录")
    @GetMapping("/extensions/node/{nodeInstanceId}")
    public Result<List<CaseNodeExtension>> getExtensionsByNodeInstanceId(
            @Parameter(description = "节点实例ID") @PathVariable Long nodeInstanceId) {
        return Result.success(caseNodeExtensionService.getExtensionsByNodeInstanceId(nodeInstanceId));
    }

    @Operation(summary = "查询案件的延期申请记录")
    @GetMapping("/extensions/case/{caseId}")
    public Result<List<CaseNodeExtension>> getExtensionsByCaseId(
            @Parameter(description = "案件ID") @PathVariable Long caseId) {
        return Result.success(caseNodeExtensionService.getExtensionsByCaseId(caseId));
    }

    @Operation(summary = "查询待审批的延期申请")
    @GetMapping("/extensions/pending")
    public Result<List<CaseNodeExtension>> getPendingApprovals() {
        return Result.success(caseNodeExtensionService.getPendingApprovals());
    }

    // ==================== 辅助方法 ====================

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long) authentication.getPrincipal();
        }
        throw new RuntimeException("无法获取当前用户ID，请先登录");
    }

    private String getCurrentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }
        return "unknown";
    }
}
