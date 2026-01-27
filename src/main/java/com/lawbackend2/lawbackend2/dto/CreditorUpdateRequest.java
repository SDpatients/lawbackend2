package com.lawbackend2.lawbackend2.dto;

import com.lawbackend2.lawbackend2.enums.CreditorStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreditorUpdateRequest {
    @NotNull(message = "债权人ID不能为空")
    private Long creditorId;

    private String creditorName;

    private String creditorType;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String idNumber;

    private String legalRepresentative;

    private java.math.BigDecimal registeredCapital;

    private CreditorStatus status;
}
