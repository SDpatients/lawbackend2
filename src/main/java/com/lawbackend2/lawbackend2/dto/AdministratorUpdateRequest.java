package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class AdministratorUpdateRequest {

    private String contactPhone;

    private String contactEmail;

    private String officeAddress;
}
