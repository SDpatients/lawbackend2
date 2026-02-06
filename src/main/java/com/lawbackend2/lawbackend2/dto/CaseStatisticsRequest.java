package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CaseStatisticsRequest {
    private LocalDate startDate;

    private LocalDate endDate;

    private Long courtId;

    private String caseStatus;

    private String caseProgress;

    private Long userId;
}
