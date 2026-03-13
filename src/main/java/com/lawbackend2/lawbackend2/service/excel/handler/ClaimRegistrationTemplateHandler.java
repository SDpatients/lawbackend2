package com.lawbackend2.lawbackend2.service.excel.handler;

import com.lawbackend2.lawbackend2.dto.ClaimRegistrationCreateRequest;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.service.ClaimRegistrationService;
import com.lawbackend2.lawbackend2.service.excel.AbstractTemplateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债权申报模板处理器
 */
@Slf4j
@Component
public class ClaimRegistrationTemplateHandler extends AbstractTemplateHandler {

    @Autowired
    private ClaimRegistrationService claimRegistrationService;

    @Override
    public String getTemplateCode() {
        return "claim_registration";
    }

    @Override
    public String getTemplateName() {
        return "债权申报模板";
    }

    @Override
    public Map<String, String> getDefaultFieldMappings() {
        Map<String, String> mappings = new LinkedHashMap<>();
        mappings.put("案件名称", "caseName");
        mappings.put("债务人", "debtor");
        mappings.put("债权人名称", "creditorName");
        mappings.put("债权人类型", "creditorType");
        mappings.put("统一社会信用代码", "creditCode");
        mappings.put("法定代表人", "legalRepresentative");
        mappings.put("送达地址", "serviceAddress");
        mappings.put("代理人姓名", "agentName");
        mappings.put("代理人电话", "agentPhone");
        mappings.put("代理人身份证", "agentIdCard");
        mappings.put("代理人地址", "agentAddress");
        mappings.put("账户名称", "accountName");
        mappings.put("债权人银行账号", "creditorBankAccount");
        mappings.put("开户行", "bankName");
        mappings.put("本金", "principal");
        mappings.put("利息", "interest");
        mappings.put("违约金", "penalty");
        mappings.put("其他损失", "otherLosses");
        mappings.put("总金额", "totalAmount");
        mappings.put("是否有法院判决", "hasCourtJudgment");
        mappings.put("是否有执行", "hasExecution");
        mappings.put("是否有担保", "hasCollateral");
        mappings.put("债权性质", "claimNature");
        mappings.put("债权类型", "claimType");
        mappings.put("债权事实", "claimFacts");
        mappings.put("债权标识", "claimIdentifier");
        mappings.put("证据清单", "evidenceList");
        mappings.put("备注", "remarks");
        return mappings;
    }

    @Override
    public List<Map<String, Object>> exportData(Long caseId, String status) {
        List<ClaimRegistration> claims = claimRegistrationService.getClaimList(1, 10000, caseId, status);
        List<Map<String, Object>> result = new ArrayList<>();

        for (ClaimRegistration claim : claims) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("caseName", claim.getCaseName());
            row.put("debtor", claim.getDebtor());
            row.put("creditorName", claim.getCreditorName());
            row.put("creditorType", claim.getCreditorType());
            row.put("creditCode", claim.getCreditCode());
            row.put("legalRepresentative", claim.getLegalRepresentative());
            row.put("serviceAddress", claim.getServiceAddress());
            row.put("agentName", claim.getAgentName());
            row.put("agentPhone", claim.getAgentPhone());
            row.put("agentIdCard", claim.getAgentIdCard());
            row.put("agentAddress", claim.getAgentAddress());
            row.put("accountName", claim.getAccountName());
            row.put("creditorBankAccount", claim.getCreditorBankAccount());
            row.put("bankName", claim.getBankName());
            row.put("principal", claim.getPrincipal());
            row.put("interest", claim.getInterest());
            row.put("penalty", claim.getPenalty());
            row.put("otherLosses", claim.getOtherLosses());
            row.put("totalAmount", claim.getTotalAmount());
            row.put("hasCourtJudgment", claim.getHasCourtJudgment() != null && claim.getHasCourtJudgment() ? "是" : "否");
            row.put("hasExecution", claim.getHasExecution() != null && claim.getHasExecution() ? "是" : "否");
            row.put("hasCollateral", claim.getHasCollateral() != null && claim.getHasCollateral() ? "是" : "否");
            row.put("claimNature", claim.getClaimNature());
            row.put("claimType", claim.getClaimType());
            row.put("claimFacts", claim.getClaimFacts());
            row.put("claimIdentifier", claim.getClaimIdentifier());
            row.put("evidenceList", claim.getEvidenceList());
            row.put("remarks", claim.getRemarks());
            result.add(row);
        }

        return result;
    }

    @Override
    public String getExportFileName() {
        return "债权申报导出";
    }

    @Override
    public String getExportSheetName() {
        return "债权申报数据";
    }

    @Override
    public String validateRow(Map<String, String> row) {
        String creditorName = getStringValue(row, "creditorName", "债权人名称", "债权人");
        if (creditorName == null || creditorName.trim().isEmpty()) {
            return "债权人名称不能为空";
        }
        return null;
    }

    @Override
    protected void saveRow(Map<String, String> row, Long caseId, Long userId) {
        ClaimRegistrationCreateRequest request = new ClaimRegistrationCreateRequest();
        request.setCaseId(caseId);
        request.setCaseName(getStringValue(row, "caseName", "案件名称"));
        request.setDebtor(getStringValue(row, "debtor", "债务人"));
        request.setCreditorName(getStringValue(row, "creditorName", "债权人名称", "债权人"));
        request.setCreditorType(getStringValue(row, "creditorType", "债权人类型", "性质"));
        request.setCreditCode(getStringValue(row, "creditCode", "统一社会信用代码"));
        request.setLegalRepresentative(getStringValue(row, "legalRepresentative", "法定代表人"));
        request.setServiceAddress(getStringValue(row, "serviceAddress", "送达地址", "地址"));
        request.setAgentName(getStringValue(row, "agentName", "代理人姓名", "代理人"));
        request.setAgentPhone(getStringValue(row, "agentPhone", "代理人电话", "联系电话"));
        request.setAgentIdCard(getStringValue(row, "agentIdCard", "代理人身份证"));
        request.setAgentAddress(getStringValue(row, "agentAddress", "代理人地址"));
        request.setAccountName(getStringValue(row, "accountName", "账户名称", "开户名"));
        request.setCreditorBankAccount(getStringValue(row, "creditorBankAccount", "债权人银行账号", "银行账号", "账号"));
        request.setBankName(getStringValue(row, "bankName", "开户行", "开户银行"));
        request.setPrincipal(getBigDecimalValue(row, "principal", "本金"));
        request.setInterest(getBigDecimalValue(row, "interest", "利息"));
        request.setPenalty(getBigDecimalValue(row, "penalty", "违约金", "罚息"));
        request.setOtherLosses(getBigDecimalValue(row, "otherLosses", "其他损失", "其他费用"));
        request.setTotalAmount(getBigDecimalValue(row, "totalAmount", "总金额", "申报金额", "债权金额"));
        request.setHasCourtJudgment(getBooleanValue(row, "hasCourtJudgment", "是否有法院判决", "涉讼"));
        request.setHasExecution(getBooleanValue(row, "hasExecution", "是否有执行"));
        request.setHasCollateral(getBooleanValue(row, "hasCollateral", "是否有担保"));
        request.setClaimNature(getStringValue(row, "claimNature", "债权性质"));
        request.setClaimType(getStringValue(row, "claimType", "债权类型", "债权种类"));
        request.setClaimFacts(getStringValue(row, "claimFacts", "债权事实"));
        request.setClaimIdentifier(getStringValue(row, "claimIdentifier", "债权标识"));
        request.setEvidenceList(getStringValue(row, "evidenceList", "证据清单"));
        request.setRemarks(getStringValue(row, "remarks", "备注"));

        // 设置默认值
        if (request.getCreditorType() == null) {
            request.setCreditorType("企业");
        }
        if (request.getClaimType() == null) {
            request.setClaimType("普通债权");
        }
        if (request.getTotalAmount() == null) {
            request.setTotalAmount(BigDecimal.ZERO);
        }

        claimRegistrationService.createClaim(request, userId);
    }
}
