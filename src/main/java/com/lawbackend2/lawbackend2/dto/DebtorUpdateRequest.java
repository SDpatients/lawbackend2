package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class DebtorUpdateRequest {

    private String enterpriseName;

    private String legalRepresentative;

    private String contactPhone;

    private String contactPerson;

    private String businessScope;

    private String industry;

    private String registeredAddress;
}
