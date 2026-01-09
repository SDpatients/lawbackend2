package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class CourtUpdateRequest {

    private String fullName;

    private String shortName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    private String undertakingJudge;
}
