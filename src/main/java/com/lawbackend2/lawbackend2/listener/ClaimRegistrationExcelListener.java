package com.lawbackend2.lawbackend2.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lawbackend2.lawbackend2.dto.ClaimRegistrationCreateRequest;
import com.lawbackend2.lawbackend2.dto.ExcelImportResponse;
import com.lawbackend2.lawbackend2.entity.ClaimRegistration;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ClaimRegistrationExcelListener extends AnalysisEventListener<ClaimRegistrationExcelImportDTO> {

    private final Long caseId;
    private final Long userId;
    private final List<ClaimRegistrationCreateRequest> successList;
    private final List<ExcelImportResponse.ImportError> errorList;
    private final List<ClaimRegistration> savedClaims;

    public ClaimRegistrationExcelListener(Long caseId, Long userId) {
        this.caseId = caseId;
        this.userId = userId;
        this.successList = new ArrayList<>();
        this.errorList = new ArrayList<>();
        this.savedClaims = new ArrayList<>();
    }

    @Override
    public void invoke(ClaimRegistrationExcelImportDTO data, AnalysisContext context) {
        int rowNum = context.readRowHolder().getRowIndex() + 1;

        try {
            ClaimRegistrationCreateRequest request = convertToCreateRequest(data);
            validateRequest(request);
            successList.add(request);
            log.info("Excel行{}数据验证通过: 债权人={}", rowNum, request.getCreditorName());
        } catch (Exception e) {
            ExcelImportResponse.ImportError error = new ExcelImportResponse.ImportError(
                    rowNum,
                    e.getMessage(),
                    data.getCreditorName() != null ? data.getCreditorName() : "未知"
            );
            errorList.add(error);
            log.error("Excel行{}数据验证失败: {}", rowNum, e.getMessage());
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成，成功{}条，失败{}条", successList.size(), errorList.size());
    }

    public List<ClaimRegistrationCreateRequest> getSuccessList() {
        return successList;
    }

    public List<ExcelImportResponse.ImportError> getErrorList() {
        return errorList;
    }

    public List<ClaimRegistration> getSavedClaims() {
        return savedClaims;
    }

    public void addSavedClaim(ClaimRegistration claim) {
        savedClaims.add(claim);
    }

    private ClaimRegistrationCreateRequest convertToCreateRequest(ClaimRegistrationExcelImportDTO data) {
        ClaimRegistrationCreateRequest request = new ClaimRegistrationCreateRequest();

        request.setCaseId(caseId);
        request.setCaseName(data.getCaseName());
        request.setDebtor(data.getDebtor());
        request.setCreditorName(data.getCreditorName());
        request.setCreditorType(data.getCreditorType());
        request.setCreditCode(data.getCreditCode());
        request.setLegalRepresentative(data.getLegalRepresentative());
        request.setServiceAddress(data.getServiceAddress());
        request.setAgentName(data.getAgentName());
        request.setAgentPhone(data.getAgentPhone());
        request.setAgentIdCard(data.getAgentIdCard());
        request.setAgentAddress(data.getAgentAddress());
        request.setAccountName(data.getAccountName());
        request.setCreditorBankAccount(data.getCreditorBankAccount());
        request.setBankName(data.getBankName());
        request.setPrincipal(data.getPrincipal());
        request.setInterest(data.getInterest());
        request.setPenalty(data.getPenalty());
        request.setOtherLosses(data.getOtherLosses());
        request.setTotalAmount(data.getTotalAmount());
        request.setHasCourtJudgment(parseBoolean(data.getHasCourtJudgment()));
        request.setHasExecution(parseBoolean(data.getHasExecution()));
        request.setHasCollateral(parseBoolean(data.getHasCollateral()));
        request.setClaimNature(data.getClaimNature());
        request.setClaimType(data.getClaimType());
        request.setClaimFacts(data.getClaimFacts());
        request.setClaimIdentifier(data.getClaimIdentifier());
        request.setEvidenceList(data.getEvidenceList());
        request.setEvidenceMaterials(data.getEvidenceMaterials());
        request.setEvidenceAttachments(data.getEvidenceAttachments());
        request.setRegistrationDate(data.getRegistrationDate());
        request.setRegistrationDeadline(data.getRegistrationDeadline());
        request.setMaterialReceiver(data.getMaterialReceiver());
        request.setMaterialReceiveDate(data.getMaterialReceiveDate());
        request.setMaterialCompleteness(data.getMaterialCompleteness());
        request.setRemarks(data.getRemarks());

        return request;
    }

    private Boolean parseBoolean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        String trimmed = value.trim().toLowerCase();
        if ("是".equals(trimmed) || "yes".equals(trimmed) || "true".equals(trimmed) || "1".equals(trimmed)) {
            return true;
        }
        return false;
    }

    private void validateRequest(ClaimRegistrationCreateRequest request) {
        if (request.getCreditorName() == null || request.getCreditorName().trim().isEmpty()) {
            throw new BusinessException("债权人名称不能为空");
        }
        if (request.getCreditorType() == null || request.getCreditorType().trim().isEmpty()) {
            throw new BusinessException("债权人类型不能为空");
        }
        if (request.getClaimType() == null || request.getClaimType().trim().isEmpty()) {
            throw new BusinessException("债权类型不能为空");
        }
        if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("总金额必须大于0");
        }
    }
}
