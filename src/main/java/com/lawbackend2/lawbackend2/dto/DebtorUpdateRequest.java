package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class DebtorUpdateRequest {

    private String enterpriseName;

    private String legalRepresentative;

    private String contactPhone;

    private String contactPerson;

    private String businessScope;

    private String industry;

    private String registeredAddress;

    private String unifiedSocialCreditCode;

    private LocalDate establishmentDate;

    private String registrationAuthority;

    private String enterpriseType;

    private String status;
}
