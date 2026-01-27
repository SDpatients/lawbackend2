package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CaseSubmitReviewRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;
}
