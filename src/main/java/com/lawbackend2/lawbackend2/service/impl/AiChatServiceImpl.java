package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.request.AiChatRequest;
import com.lawbackend2.lawbackend2.dto.response.AiChatMessageResponse;
import com.lawbackend2.lawbackend2.dto.response.AiChatSessionResponse;
import com.lawbackend2.lawbackend2.dto.response.CaseRelatedDataResponse;
import com.lawbackend2.lawbackend2.entity.AiChatMessage;
import com.lawbackend2.lawbackend2.entity.AiChatSession;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.entity.CaseProgress;
import com.lawbackend2.lawbackend2.entity.CommonDebt;
import com.lawbackend2.lawbackend2.entity.FundAccount;
import com.lawbackend2.lawbackend2.repository.AiChatMessageRepository;
import com.lawbackend2.lawbackend2.repository.AiChatSessionRepository;
import com.lawbackend2.lawbackend2.repository.CaseProgressRepository;
import com.lawbackend2.lawbackend2.repository.CommonDebtRepository;
import com.lawbackend2.lawbackend2.repository.FundAccountRepository;
import com.lawbackend2.lawbackend2.service.AiChatService;
import com.lawbackend2.lawbackend2.service.BankruptCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private final AiChatSessionRepository sessionRepository;
    private final AiChatMessageRepository messageRepository;
    private final BankruptCaseService bankruptCaseService;
    private final CommonDebtRepository commonDebtRepository;
    private final FundAccountRepository fundAccountRepository;
    private final CaseProgressRepository caseProgressRepository;

    @Value("${ai.gitee.api-url:https://ai.gitee.com/v1/chat/completions}")
    private String aiApiUrl;

    @Value("${ai.gitee.api-key:}")
    private String aiApiKey;

    @Value("${ai.gitee.model:LegalOne-8B}")
    private String aiModel;

    private static final int MAX_HISTORY_MESSAGES = 20;

    @Autowired
    public AiChatServiceImpl(AiChatSessionRepository sessionRepository,
                             AiChatMessageRepository messageRepository,
                             BankruptCaseService bankruptCaseService,
                             CommonDebtRepository commonDebtRepository,
                             FundAccountRepository fundAccountRepository,
                             CaseProgressRepository caseProgressRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.bankruptCaseService = bankruptCaseService;
        this.commonDebtRepository = commonDebtRepository;
        this.fundAccountRepository = fundAccountRepository;
        this.caseProgressRepository = caseProgressRepository;
    }

    @Override
    @Transactional
    public AiChatMessageResponse sendMessage(AiChatRequest request) {
        AiChatSession session = resolveSession(request);

        AiChatMessage userMessage = createUserMessage(session, request.getContent(), request.getUserId());
        messageRepository.save(userMessage);

        String aiResponseContent = generateAiResponse(request.getContent(), request.getCaseId(), session.getId());

        AiChatMessage aiMessage = createAiMessage(session, aiResponseContent);
        messageRepository.save(aiMessage);

        updateSessionInfo(session, aiMessage.getTimestamp());

        return convertToResponse(aiMessage);
    }

    private AiChatSession resolveSession(AiChatRequest request) {
        if (request.getSessionId() != null) {
            Optional<AiChatSession> existingSession = sessionRepository.findByIdAndCaseIdAndUserId(
                    request.getSessionId(), request.getCaseId(), request.getUserId());
            if (existingSession.isPresent()) {
                return existingSession.get();
            }
            log.warn("指定的会话不存在，sessionId: {}, 将创建新会话", request.getSessionId());
        }
        return createNewSession(request.getCaseId(), request.getUserId());
    }

    private AiChatSession createNewSession(Long caseId, Long userId) {
        int count = sessionRepository.countByCaseIdAndUserId(caseId, userId);
        AiChatSession newSession = new AiChatSession();
        newSession.setCaseId(caseId);
        newSession.setUserId(userId);
        newSession.setSessionName("新对话 " + (count + 1));
        newSession.setMessageCount(0);
        newSession.setCreateUserId(userId);
        newSession.setUpdateUserId(userId);
        return sessionRepository.save(newSession);
    }

    /**
     * 流式生成AI回复（用于前端流式显示）
     * @param request 聊天请求
     * @param callback 回调函数，用于逐段返回AI回复
     * @throws Exception 异常信息
     */
    public void sendMessageStream(AiChatRequest request, java.util.function.Consumer<String> callback) throws Exception {
        AiChatSession session = resolveSession(request);
        
        AiChatMessage userMessage = createUserMessage(session, request.getContent(), request.getUserId());
        messageRepository.save(userMessage);

        String aiResponseContent = generateAiResponse(request.getContent(), request.getCaseId(), session.getId());
        
        // 安全检查
        if (aiResponseContent == null || aiResponseContent.trim().isEmpty()) {
            aiResponseContent = "抱歉，AI未能生成回复，请重试。";
        }
        
        // 按段落/句子批量发送，更稳健的策略
        String[] chunks = splitIntoChunks(aiResponseContent);
        StringBuilder responseBuilder = new StringBuilder();
        
        for (String chunk : chunks) {
            responseBuilder.append(chunk);
            callback.accept(responseBuilder.toString());
            Thread.sleep(100); // 每次间隔 100ms，更稳健
        }
        
        // 确保最终内容被发送
        if (responseBuilder.length() < aiResponseContent.length()) {
            callback.accept(aiResponseContent);
        }
        
        AiChatMessage aiMessage = createAiMessage(session, aiResponseContent);
        messageRepository.save(aiMessage);

        updateSessionInfo(session, aiMessage.getTimestamp());
    }

    @Override
    public List<AiChatMessageResponse> getChatHistory(Long caseId) {
        List<AiChatMessage> messages = messageRepository.findByCaseIdOrderByTimestampAsc(caseId);
        List<AiChatMessageResponse> responses = new ArrayList<>();
        
        for (AiChatMessage message : messages) {
            responses.add(convertToResponse(message));
        }
        
        return responses;
    }

    @Override
    public List<AiChatMessageResponse> getMessagesBySessionId(Long sessionId) {
        List<AiChatMessage> messages = messageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
        List<AiChatMessageResponse> responses = new ArrayList<>();
        for (AiChatMessage message : messages) {
            responses.add(convertToResponse(message));
        }
        return responses;
    }

    @Override
    public List<AiChatSessionResponse> listSessions(Long caseId, Long userId) {
        List<AiChatSession> sessions = sessionRepository.findByCaseIdAndUserIdOrderByCreateTimeDesc(caseId, userId);
        return sessions.stream()
                .map(this::convertToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AiChatSessionResponse createSession(Long caseId, Long userId, String sessionName) {
        AiChatSession session = new AiChatSession();
        session.setCaseId(caseId);
        session.setUserId(userId);
        session.setSessionName(sessionName != null && !sessionName.trim().isEmpty() ? sessionName.trim() : "新对话");
        session.setMessageCount(0);
        session.setCreateUserId(userId);
        session.setUpdateUserId(userId);
        sessionRepository.save(session);
        return convertToSessionResponse(session);
    }

    @Override
    @Transactional
    public AiChatSessionResponse renameSession(Long sessionId, String sessionName, Long userId) {
        Optional<AiChatSession> optional = sessionRepository.findById(sessionId);
        if (!optional.isPresent()) {
            throw new RuntimeException("会话不存在");
        }
        AiChatSession session = optional.get();
        if (sessionName != null && !sessionName.trim().isEmpty()) {
            session.setSessionName(sessionName.trim());
        }
        session.setUpdateUserId(userId);
        session.setUpdateTime(LocalDateTime.now());
        sessionRepository.save(session);
        return convertToSessionResponse(session);
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId, Long userId) {
        Optional<AiChatSession> optional = sessionRepository.findById(sessionId);
        if (!optional.isPresent()) {
            throw new RuntimeException("会话不存在");
        }
        AiChatSession session = optional.get();
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该会话");
        }
        messageRepository.deleteBySessionId(sessionId);
        sessionRepository.deleteSessionById(sessionId);
    }

    @Override
    public AiChatSession getOrCreateSession(Long caseId, Long userId) {
        List<AiChatSession> sessions = sessionRepository.findByCaseIdAndUserIdOrderByCreateTimeDesc(caseId, userId);
        if (!sessions.isEmpty()) {
            return sessions.get(0);
        }
        return createNewSession(caseId, userId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String generateAiResponse(String userMessage, Long caseId, Long sessionId) {
        try {
            String systemPrompt = buildCaseContextPrompt(caseId);
            List<Map<String, String>> historyMessages = buildConversationHistory(sessionId);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiModel);

            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);

            messages.addAll(historyMessages);

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    aiApiUrl,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        return (String) message.get("content");
                    }
                }
            }

            log.error("AI API调用失败，状态码：{}", response.getStatusCode());
            return "抱歉，AI服务暂时不可用，请稍后重试。";

        } catch (Exception e) {
            log.error("AI API调用异常", e);
            return "抱歉，AI服务暂时不可用，请稍后重试。";
        }
    }

    private String buildCaseContextPrompt(Long caseId) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的破产案件法律助手，负责回答用户关于案件的问题。请根据用户的问题，结合以下案件信息提供专业、准确的法律建议。\n\n");

        try {
            BankruptCase bankruptCase = bankruptCaseService.getCaseById(caseId);
            if (bankruptCase != null) {
                prompt.append("=== 案件基本信息 ===\n");
                prompt.append("案号：").append(nullToEmpty(bankruptCase.getCaseNumber())).append("\n");
                prompt.append("案件名称：").append(nullToEmpty(bankruptCase.getCaseName())).append("\n");
                prompt.append("案件类型：").append(formatCaseType(bankruptCase.getCaseType())).append("\n");
                prompt.append("案件状态：").append(formatCaseStatus(bankruptCase.getCaseStatus())).append("\n");
                prompt.append("案件进度：").append(nullToEmpty(bankruptCase.getCaseProgress())).append("\n");
                prompt.append("受理法院：").append(nullToEmpty(bankruptCase.getAcceptanceCourt())).append("\n");
                prompt.append("承办法官：").append(nullToEmpty(bankruptCase.getDesignatedJudge())).append("\n");
                prompt.append("指定机构：").append(nullToEmpty(bankruptCase.getDesignatedInstitution())).append("\n");
                prompt.append("主要负责人：").append(nullToEmpty(bankruptCase.getMainResponsiblePerson())).append("\n");
                prompt.append("案由：").append(nullToEmpty(bankruptCase.getCaseReason())).append("\n");
                if (bankruptCase.getAcceptanceDate() != null) {
                    prompt.append("受理日期：").append(bankruptCase.getAcceptanceDate()).append("\n");
                }
                if (bankruptCase.getIsSimplifiedTrial() != null && bankruptCase.getIsSimplifiedTrial()) {
                    prompt.append("简易审判程序：是\n");
                }
                prompt.append("\n");
            }
        } catch (Exception e) {
            log.warn("获取案件基本信息失败，caseId: {}", caseId, e);
        }

        try {
            CaseRelatedDataResponse relatedData = bankruptCaseService.getCaseRelatedData(caseId);
            if (relatedData != null) {
                prompt.append("=== 案件关联数据统计 ===\n");

                if (relatedData.getClaimData() != null) {
                    CaseRelatedDataResponse.ClaimData claim = relatedData.getClaimData();
                    prompt.append("债权信息：债权人").append(nullToZero(claim.getCreditorInfoCount())).append("名，")
                          .append("债权申报").append(nullToZero(claim.getCreditorClaimCount())).append("笔，")
                          .append("申报登记").append(nullToZero(claim.getClaimRegistrationCount())).append("条，")
                          .append("审核记录").append(nullToZero(claim.getClaimReviewCount())).append("条，")
                          .append("确认记录").append(nullToZero(claim.getClaimConfirmationCount())).append("条\n");
                }

                if (relatedData.getDebtData() != null) {
                    CaseRelatedDataResponse.DebtData debt = relatedData.getDebtData();
                    prompt.append("共益债务：").append(nullToZero(debt.getCommonDebtCount())).append("笔\n");
                }

                if (relatedData.getFundData() != null) {
                    CaseRelatedDataResponse.FundData fund = relatedData.getFundData();
                    prompt.append("资金管理：账户").append(nullToZero(fund.getFundAccountCount())).append("个，")
                          .append("流水").append(nullToZero(fund.getFundFlowCount())).append("条，")
                          .append("预算").append(nullToZero(fund.getFundBudgetCount())).append("条，")
                          .append("审批").append(nullToZero(fund.getFundApprovalCount())).append("条，")
                          .append("报销").append(nullToZero(fund.getFundReimbursementCount())).append("条，")
                          .append("提存").append(nullToZero(fund.getEscrowManagementCount())).append("条，")
                          .append("破产费用").append(nullToZero(fund.getBankruptcyExpenseCount())).append("条\n");
                }

                if (relatedData.getDistributionData() != null) {
                    CaseRelatedDataResponse.DistributionData dist = relatedData.getDistributionData();
                    prompt.append("财产分配：明细").append(nullToZero(dist.getDistributionDetailCount())).append("条，")
                          .append("执行").append(nullToZero(dist.getDistributionExecutionCount())).append("条\n");
                }

                if (relatedData.getWorkData() != null) {
                    CaseRelatedDataResponse.WorkData work = relatedData.getWorkData();
                    prompt.append("工作管理：管理人").append(nullToZero(work.getAdministratorCount())).append("名，")
                          .append("工作组").append(nullToZero(work.getWorkTeamCount())).append("个，")
                          .append("工作计划").append(nullToZero(work.getWorkPlanCount())).append("条，")
                          .append("工作日志").append(nullToZero(work.getWorkLogCount())).append("条，")
                          .append("案件进展").append(nullToZero(work.getCaseProgressCount())).append("条\n");
                }

                if (relatedData.getTaskData() != null) {
                    CaseRelatedDataResponse.TaskData task = relatedData.getTaskData();
                    prompt.append("案件任务：任务").append(nullToZero(task.getCaseTaskCount())).append("条，")
                          .append("提交").append(nullToZero(task.getCaseTaskSubmissionCount())).append("条\n");
                }

                if (relatedData.getApprovalData() != null) {
                    CaseRelatedDataResponse.ApprovalData approval = relatedData.getApprovalData();
                    prompt.append("审批数量：").append(nullToZero(approval.getApprovalCount())).append("条，")
                          .append("审批历史").append(nullToZero(approval.getApprovalHistoryCount())).append("条\n");
                }

                if (relatedData.getAnnouncementData() != null) {
                    CaseRelatedDataResponse.AnnouncementData ann = relatedData.getAnnouncementData();
                    prompt.append("公告管理：公告").append(nullToZero(ann.getAnnouncementCount())).append("条，")
                          .append("查看记录").append(nullToZero(ann.getAnnouncementViewCount())).append("条\n");
                }

                if (relatedData.getAccountData() != null) {
                    prompt.append("银行账户：").append(nullToZero(relatedData.getAccountData().getBankAccountCount())).append("个\n");
                }

                if (relatedData.getEnterpriseData() != null) {
                    prompt.append("债务人企业：").append(nullToZero(relatedData.getEnterpriseData().getDebtorEnterpriseCount())).append("家\n");
                }

                if (relatedData.getDocumentData() != null) {
                    prompt.append("文书送达：").append(nullToZero(relatedData.getDocumentData().getDocumentDeliveryCount())).append("条\n");
                }

                prompt.append("\n");
            }
        } catch (Exception e) {
            log.warn("获取案件关联数据失败，caseId: {}", caseId, e);
        }

        try {
            List<CommonDebt> debts = commonDebtRepository.findByCaseId(caseId);
            if (debts != null && !debts.isEmpty()) {
                BigDecimal totalDebt = debts.stream()
                        .map(CommonDebt::getDebtAmount)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalUnrepaid = debts.stream()
                        .map(CommonDebt::getUnrepaidAmount)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                long overdueCount = debts.stream().filter(d -> d.getIsOverdue() != null && d.getIsOverdue()).count();

                prompt.append("=== 债务金额详情 ===\n");
                prompt.append("债务总额：").append(totalDebt.toPlainString()).append(" 元\n");
                prompt.append("未偿还总额：").append(totalUnrepaid.toPlainString()).append(" 元\n");
                prompt.append("逾期债务数量：").append(overdueCount).append(" 笔\n\n");
            }
        } catch (Exception e) {
            log.warn("获取债务详情失败，caseId: {}", caseId, e);
        }

        try {
            List<FundAccount> fundAccounts = fundAccountRepository.findByCaseId(caseId);
            if (fundAccounts != null && !fundAccounts.isEmpty()) {
                BigDecimal totalBalance = fundAccounts.stream()
                        .map(FundAccount::getCurrentBalance)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalInitial = fundAccounts.stream()
                        .map(FundAccount::getInitialBalance)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                long frozenCount = fundAccounts.stream().filter(f -> f.getIsFrozen() != null && f.getIsFrozen()).count();

                prompt.append("=== 资金账户详情 ===\n");
                prompt.append("账户数量：").append(fundAccounts.size()).append(" 个\n");
                prompt.append("当前总余额：").append(totalBalance.toPlainString()).append(" 元\n");
                prompt.append("初始总余额：").append(totalInitial.toPlainString()).append(" 元\n");
                prompt.append("冻结账户数：").append(frozenCount).append(" 个\n\n");
            }
        } catch (Exception e) {
            log.warn("获取资金账户详情失败，caseId: {}", caseId, e);
        }

        try {
            List<CaseProgress> progresses = caseProgressRepository.findByCaseIdOrderByStartDate(caseId);
            if (progresses != null && !progresses.isEmpty()) {
                List<CaseProgress> inProgress = progresses.stream()
                        .filter(p -> p.getIsCompleted() == null || !p.getIsCompleted())
                        .collect(Collectors.toList());
                List<CaseProgress> completed = progresses.stream()
                        .filter(p -> p.getIsCompleted() != null && p.getIsCompleted())
                        .collect(Collectors.toList());

                prompt.append("=== 案件进度详情 ===\n");
                prompt.append("总进度阶段：").append(progresses.size()).append(" 个\n");
                prompt.append("已完成：").append(completed.size()).append(" 个，进行中：").append(inProgress.size()).append(" 个\n");

                if (!inProgress.isEmpty()) {
                    prompt.append("当前进行中的阶段：\n");
                    for (CaseProgress cp : inProgress) {
                        prompt.append("  - ").append(nullToEmpty(cp.getStageName()))
                              .append("（").append(nullToEmpty(cp.getProgressStage())).append("）");
                        if (cp.getCompletionPercentage() != null) {
                            prompt.append(" 完成度：").append(cp.getCompletionPercentage()).append("%");
                        }
                        if (cp.getResponsiblePerson() != null) {
                            prompt.append(" 负责人：").append(cp.getResponsiblePerson());
                        }
                        prompt.append("\n");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取案件进度详情失败，caseId: {}", caseId, e);
        }

        prompt.append("\n请基于以上案件信息，为用户提供针对性的分析和建议。如果用户询问的信息不在以上数据范围内，请告知用户并提供一般性的法律指导。");
        return prompt.toString();
    }

    private List<Map<String, String>> buildConversationHistory(Long sessionId) {
        List<Map<String, String>> historyMessages = new ArrayList<>();
        if (sessionId == null) {
            return historyMessages;
        }

        try {
            List<AiChatMessage> messages = messageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
            if (messages != null && !messages.isEmpty()) {
                int startIndex = Math.max(0, messages.size() - MAX_HISTORY_MESSAGES);
                for (int i = startIndex; i < messages.size(); i++) {
                    AiChatMessage msg = messages.get(i);
                    Map<String, String> historyMsg = new HashMap<>();
                    if ("user".equals(msg.getSenderType())) {
                        historyMsg.put("role", "user");
                    } else if ("ai".equals(msg.getSenderType())) {
                        historyMsg.put("role", "assistant");
                    } else {
                        continue;
                    }
                    historyMsg.put("content", msg.getContent());
                    historyMessages.add(historyMsg);
                }
            }
        } catch (Exception e) {
            log.warn("获取对话历史失败，sessionId: {}", sessionId, e);
        }

        return historyMessages;
    }

    private String nullToEmpty(String value) {
        return value == null ? "未知" : value;
    }

    private String nullToZero(Integer value) {
        return value == null ? "0" : String.valueOf(value);
    }

    private String formatCaseType(String caseType) {
        if (caseType == null) return "未知";
        switch (caseType) {
            case "LIQUIDATION": return "清算";
            case "REORGANIZATION": return "重整";
            case "SETTLEMENT": return "和解";
            default: return caseType;
        }
    }

    private String formatCaseStatus(String caseStatus) {
        if (caseStatus == null) return "未知";
        switch (caseStatus) {
            case "ONGOING": return "进行中";
            case "CLOSED": return "已结案";
            case "ARCHIVED": return "已归档";
            default: return caseStatus;
        }
    }

    /**
     * 将 AI 响应内容分割成自然的块，便于前端流式展示
     * 策略：按段落或句子分块
     */
    private String[] splitIntoChunks(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new String[]{""};
        }
        
        List<String> chunks = new ArrayList<>();
        // 优先按换行符分割
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                chunks.add("\n");
                continue;
            }
            
            // 对于长行，按句子进一步分割
            if (line.length() > 100) {
                String[] sentences = line.split("(?<=[。！？.，,])"); // 在标点后分割
                for (String sentence : sentences) {
                    if (sentence.length() > 50) {
                        // 对于太长的句子，进一步按词块分割
                        int start = 0;
                        while (start < sentence.length()) {
                            int end = Math.min(start + 50, sentence.length());
                            chunks.add(sentence.substring(start, end));
                            start = end;
                        }
                    } else {
                        chunks.add(sentence);
                    }
                }
            } else {
                chunks.add(line);
            }
        }
        
        return chunks.toArray(new String[0]);
    }

    private AiChatSessionResponse convertToSessionResponse(AiChatSession session) {
        AiChatSessionResponse response = new AiChatSessionResponse();
        response.setId(session.getId());
        response.setCaseId(session.getCaseId());
        response.setUserId(session.getUserId());
        response.setSessionName(session.getSessionName());
        response.setLastMessageTime(session.getLastMessageTime());
        response.setMessageCount(session.getMessageCount());
        response.setCreateTime(session.getCreateTime());
        response.setUpdateTime(session.getUpdateTime());
        return response;
    }

    private AiChatMessage createUserMessage(AiChatSession session, String content, Long userId) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(session.getId());
        message.setCaseId(session.getCaseId());
        message.setContent(content);
        message.setSenderType("user");
        message.setSenderId(userId);
        message.setTimestamp(LocalDateTime.now());
        message.setMessageStatus("SENT");
        message.setCreateUserId(userId);
        message.setUpdateUserId(userId);
        return message;
    }

    private AiChatMessage createAiMessage(AiChatSession session, String content) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(session.getId());
        message.setCaseId(session.getCaseId());
        message.setContent(content);
        message.setSenderType("ai");
        message.setTimestamp(LocalDateTime.now());
        message.setMessageStatus("SENT");
        return message;
    }

    private void updateSessionInfo(AiChatSession session, LocalDateTime lastMessageTime) {
        session.setLastMessageTime(lastMessageTime);
        session.setMessageCount(session.getMessageCount() + 2); // 增加两条消息（用户和AI）
        session.setUpdateTime(LocalDateTime.now());
        sessionRepository.save(session);
    }

    private AiChatMessageResponse convertToResponse(AiChatMessage message) {
        AiChatMessageResponse response = new AiChatMessageResponse();
        response.setId(message.getId());
        response.setSessionId(message.getSessionId());
        response.setCaseId(message.getCaseId());
        response.setContent(message.getContent());
        response.setSender(message.getSenderType());
        response.setTimestamp(message.getTimestamp());
        response.setMessageStatus(message.getMessageStatus());
        return response;
    }
}
