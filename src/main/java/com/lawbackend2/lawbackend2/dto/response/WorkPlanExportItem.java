package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkPlanExportItem {
    private Long id;
    private String planNumber;
    private String planType;
    private String planName;
    private String executionStatus;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private String responsiblePerson;
    private String priority;
    private String status;
}
