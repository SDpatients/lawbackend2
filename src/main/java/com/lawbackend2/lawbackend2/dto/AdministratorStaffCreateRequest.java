package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdministratorStaffCreateRequest {

    @NotNull(message = "管理人ID不能为空")
    private Long administratorId;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "人员类型不能为空")
    private String staffType;

    private String idNumber;

    private String lawyerLicenseNumber;

    private String contactPhone;

    private String email;

    private String responsibility;

    private java.time.LocalDate appointmentDate;

    private Long userId;
}
