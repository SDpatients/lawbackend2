package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class CourtUpdateRequest {

    private String fullName;

    private String shortName;

    private String contactPhone;

    private String undertakingJudge;
    
    private String address;
}
