package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

@Data
public class AdministratorStaffUpdateRequest {

    private String name;

    private String staffType;

    private String idNumber;

    private String lawyerLicenseNumber;

    private String contactPhone;

    private String email;

    private String responsibility;

    private java.time.LocalDate appointmentDate;

    private Long userId;
}
