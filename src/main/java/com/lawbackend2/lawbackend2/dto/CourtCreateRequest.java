package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CourtCreateRequest {

    @NotBlank(message = "法院全称不能为空")
    private String fullName;

    @NotBlank(message = "法院简称不能为空")
    private String shortName;

    @NotBlank(message = "法院级别不能为空")
    private String courtLevel;

    private String address;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    private Long responsibleUserId;

    private String undertakingJudge;
}
