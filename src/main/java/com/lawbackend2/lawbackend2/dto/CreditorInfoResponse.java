package com.lawbackend2.lawbackend2.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lawbackend2.lawbackend2.annotation.Mask;
import com.lawbackend2.lawbackend2.annotation.MaskType;
import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import com.lawbackend2.lawbackend2.util.MaskSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditorInfoResponse {
    private Long id;
    private Long caseId;
    private String creditorName;
    private String creditorType;

    @Mask(MaskType.PHONE)
    @JsonSerialize(using = MaskSerializer.class)
    private String contactPhone;

    private String contactEmail;
    private String address;

    @Mask(MaskType.ID_CARD)
    @JsonSerialize(using = MaskSerializer.class)
    private String idNumber;
    private String legalRepresentative;
    private BigDecimal registeredCapital;
    private CreditorStatus creditorStatus;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUserId;
    private Long updateUserId;
    private Boolean isDeleted;
    
    private String caseNumber;
    private String caseName;
}