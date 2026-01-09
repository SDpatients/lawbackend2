package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreditorClaimReviewRequest {

    @NotNull(message = "登记状态不能为空")
    private String registrationStatus;

    @NotBlank(message = "审核意见不能为空")
    private String reviewOpinion;

    private String claimNatureManager;
}
