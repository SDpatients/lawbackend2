package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreditorClaimStatisticsRequest {
    private Long caseId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String registrationStatus;

    private String claimType;

    private String claimNature;
}
