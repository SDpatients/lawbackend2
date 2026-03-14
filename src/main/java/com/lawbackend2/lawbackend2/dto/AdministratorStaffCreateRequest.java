package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AdministratorStaffCreateRequest {

    private Long administratorId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String name;

    private String staffType;

    private String idNumber;

    private String lawyerLicenseNumber;

    private String contactPhone;

    private String email;

    private String responsibility;

    private java.time.LocalDate appointmentDate;
}
