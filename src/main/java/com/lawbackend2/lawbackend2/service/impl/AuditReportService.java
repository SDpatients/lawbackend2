package com.lawbackend2.lawbackend2.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawbackend2.lawbackend2.entity.AuditLog;
import com.lawbackend2.lawbackend2.repository.AuditLogRepository;
import com.lawbackend2.lawbackend2.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditReportService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

    public String generateWeeklyReportHtml(LocalDateTime startTime, LocalDateTime endTime) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<title>审计日志周报</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { color: #333; }");
        html.append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #4CAF50; color: white; }");
        html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
        html.append(".summary { background-color: #e7f3fe; padding: 15px; margin: 20px 0; border-left: 6px solid #2196F3; }");
        html.append(".warning { background-color: #fff3cd; padding: 15px; margin: 20px 0; border-left: 6px solid #ffc107; }");
        html.append(".success { background-color: #d4edda; padding: 15px; margin: 20px 0; border-left: 6px solid #28a745; }");
        html.append("</style></head><body>");

        html.append("<h1>🛡️ 审计日志周报</h1>");
        html.append("<p><strong>报告生成时间：</strong>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>");
        html.append("<p><strong>统计周期：</strong>")
                .append(startTime.format(DATE_FORMATTER))
                .append(" 至 ")
                .append(endTime.format(DATE_FORMATTER))
                .append("</p>");

        long totalCount = auditLogService.countByTimeRange(startTime, endTime);
        html.append("<div class='summary'>");
        html.append("<h2>📊 总体概况</h2>");
        html.append("<p><strong>本周总操作次数：</strong>").append(totalCount).append(" 次</p>");

        Map<String, Long> moduleStats = auditLogService.countGroupByModule(startTime, endTime);
        if (moduleStats != null && !moduleStats.isEmpty()) {
            html.append("<p><strong>涉及模块数量：</strong>").append(moduleStats.size()).append(" 个</p>");
        }

        Map<String, Long> operationStats = auditLogService.countGroupByOperationType(startTime, endTime);
        if (operationStats != null && !operationStats.isEmpty()) {
            html.append("<p><strong>操作类型数量：</strong>").append(operationStats.size()).append(" 种</p>");
        }
        html.append("</div>");

        List<AuditLog> tamperedLogs = auditLogService.verifyAllIntegrity();
        if (tamperedLogs != null && !tamperedLogs.isEmpty()) {
            html.append("<div class='warning'>");
            html.append("<h2>⚠️ 安全警告</h2>");
            html.append("<p>发现 <strong>").append(tamperedLogs.size()).append("</strong> 条审计日志存在完整性问题！</p>");
            html.append("<p>请立即检查以下日志记录，确认是否存在未授权的访问或篡改行为。</p>");
            html.append("<ul>");
            for (AuditLog log : tamperedLogs) {
                html.append("<li>日志ID: ").append(log.getId())
                        .append(", 用户: ").append(log.getUserAccount())
                        .append(", 操作: ").append(log.getOperationName())
                        .append(", 时间: ").append(log.getCreateTime().format(DATE_FORMATTER))
                        .append("</li>");
            }
            html.append("</ul>");
            html.append("</div>");
        } else {
            html.append("<div class='success'>");
            html.append("<h2>✅ 完整性验证通过</h2>");
            html.append("<p>所有审计日志均通过哈希链完整性验证，未发现篡改痕迹。</p>");
            html.append("</div>");
        }

        if (moduleStats != null && !moduleStats.isEmpty()) {
            html.append("<h2>📈 按模块统计</h2>");
            html.append("<table><tr><th>模块名称</th><th>操作次数</th><th>占比</th></tr>");
            for (Map.Entry<String, Long> entry : moduleStats.entrySet()) {
                String percentage = String.format("%.2f%%", (double) entry.getValue() / totalCount * 100);
                html.append("<tr><td>").append(entry.getKey()).append("</td>")
                        .append("<td>").append(entry.getValue()).append("</td>")
                        .append("<td>").append(percentage).append("</td></tr>");
            }
            html.append("</table>");
        }

        if (operationStats != null && !operationStats.isEmpty()) {
            html.append("<h2>📋 按操作类型统计</h2>");
            html.append("<table><tr><th>操作类型</th><th>操作次数</th><th>占比</th></tr>");
            for (Map.Entry<String, Long> entry : operationStats.entrySet()) {
                String percentage = String.format("%.2f%%", (double) entry.getValue() / totalCount * 100);
                html.append("<tr><td>").append(entry.getKey()).append("</td>")
                        .append("<td>").append(entry.getValue()).append("</td>")
                        .append("<td>").append(percentage).append("</td></tr>");
            }
            html.append("</table>");
        }

        html.append("<h2>📝 操作详情（最近50条）</h2>");
        Page<AuditLog> recentLogs = auditLogRepository.searchAuditLogs(
                null, null, null, null, null, startTime, endTime, null, Pageable.ofSize(50));
        if (recentLogs.hasContent()) {
            html.append("<table><tr><th>序号</th><th>时间</th><th>用户</th><th>模块</th><th>操作</th><th>状态</th><th>哈希值</th></tr>");
            int index = 1;
            for (AuditLog log : recentLogs.getContent()) {
                String shortHash = log.getHashValue() != null && log.getHashValue().length() > 16
                        ? log.getHashValue().substring(0, 16) + "..."
                        : (log.getHashValue() != null ? log.getHashValue() : "N/A");
                html.append("<tr><td>").append(index++).append("</td>")
                        .append("<td>").append(log.getCreateTime().format(DATE_FORMATTER)).append("</td>")
                        .append("<td>").append(log.getUserAccount() != null ? log.getUserAccount() : "系统").append("</td>")
                        .append("<td>").append(log.getModuleName() != null ? log.getModuleName() : log.getModule()).append("</td>")
                        .append("<td>").append(log.getOperationName() != null ? log.getOperationName() : log.getOperationType()).append("</td>")
                        .append("<td>").append(log.getStatus()).append("</td>")
                        .append("<td>").append(shortHash).append("</td></tr>");
            }
            html.append("</table>");
        }

        html.append("<div style='margin-top: 30px; padding: 15px; background-color: #f8f9fa; border-top: 1px solid #dee2e6;'>");
        html.append("<h3>📌 合规说明</h3>");
        html.append("<p>本报告由系统自动生成，所有审计日志均采用 SHA-256 哈希算法和链式结构存储，确保数据不可篡改。</p>");
        html.append("<p>如有任何疑问，请联系系统管理员。</p>");
        html.append("<p><strong>系统名称：</strong>法律案件管理系统<br>");
        html.append("<strong>生成时间：</strong>").append(LocalDateTime.now().format(DATE_FORMATTER)).append("</p>");
        html.append("</div>");

        html.append("</body></html>");

        return html.toString();
    }

    public String generateWeeklyReportText(LocalDateTime startTime, LocalDateTime endTime) {
        StringBuilder text = new StringBuilder();

        text.append("========================================\n");
        text.append("         审计日志周报\n");
        text.append("========================================\n\n");

        text.append("报告生成时间：").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
        text.append("统计周期：")
                .append(startTime.format(DATE_FORMATTER))
                .append(" 至 ")
                .append(endTime.format(DATE_FORMATTER))
                .append("\n\n");

        long totalCount = auditLogService.countByTimeRange(startTime, endTime);
        text.append("【总体概况】\n");
        text.append("本周总操作次数：").append(totalCount).append(" 次\n");

        Map<String, Long> moduleStats = auditLogService.countGroupByModule(startTime, endTime);
        if (moduleStats != null && !moduleStats.isEmpty()) {
            text.append("涉及模块数量：").append(moduleStats.size()).append(" 个\n");
        }

        text.append("\n");

        List<AuditLog> tamperedLogs = auditLogService.verifyAllIntegrity();
        if (tamperedLogs != null && !tamperedLogs.isEmpty()) {
            text.append("【安全警告】\n");
            text.append("发现 ").append(tamperedLogs.size()).append(" 条审计日志存在完整性问题！\n");
            text.append("请立即检查以下日志记录：\n");
            for (AuditLog log : tamperedLogs) {
                text.append("- 日志ID: ").append(log.getId())
                        .append(", 用户: ").append(log.getUserAccount())
                        .append(", 操作: ").append(log.getOperationName())
                        .append(", 时间: ").append(log.getCreateTime().format(DATE_FORMATTER))
                        .append("\n");
            }
        } else {
            text.append("【完整性验证】\n");
            text.append("所有审计日志均通过哈希链完整性验证，未发现篡改痕迹。\n");
        }

        text.append("\n【按模块统计】\n");
        if (moduleStats != null && !moduleStats.isEmpty()) {
            for (Map.Entry<String, Long> entry : moduleStats.entrySet()) {
                String percentage = String.format("%.2f%%", (double) entry.getValue() / totalCount * 100);
                text.append(entry.getKey()).append("：").append(entry.getValue()).append(" 次 (").append(percentage).append(")\n");
            }
        }

        text.append("\n【合规说明】\n");
        text.append("本报告由系统自动生成，所有审计日志均采用 SHA-256 哈希算法和链式结构存储，确保数据不可篡改。\n");
        text.append("如有任何疑问，请联系系统管理员。\n\n");
        text.append("========================================\n");

        return text.toString();
    }

    public Map<String, Object> generateWeeklyReportSummary(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> summary = new HashMap<>();

        summary.put("reportTime", LocalDateTime.now());
        summary.put("startTime", startTime);
        summary.put("endTime", endTime);
        summary.put("totalCount", auditLogService.countByTimeRange(startTime, endTime));
        summary.put("moduleStats", auditLogService.countGroupByModule(startTime, endTime));
        summary.put("operationStats", auditLogService.countGroupByOperationType(startTime, endTime));
        summary.put("integrityReport", auditLogService.getIntegrityReport());

        List<AuditLog> tamperedLogs = auditLogService.verifyAllIntegrity();
        summary.put("tamperedCount", tamperedLogs != null ? tamperedLogs.size() : 0);
        summary.put("hasIntegrityIssue", tamperedLogs != null && !tamperedLogs.isEmpty());

        return summary;
    }
}
