package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CaseCreateRequest {
    @NotBlank(message = "案号不能为空")
    private String caseNumber;

    @NotBlank(message = "案件名称不能为空")
    private String caseName;

    @NotNull(message = "受理日期不能为空")
    private LocalDate acceptanceDate;

    private String caseSource;

    private String acceptanceCourt;

    private String designatedInstitution;

    private String mainResponsiblePerson;

    private Integer isSimplifiedTrial = 0;

    private String caseReason;

    private String caseProgress = "FIRST";

    private LocalDate debtClaimDeadline;

    private LocalDate filingDate;

    private String remarks;
}
