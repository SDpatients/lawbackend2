package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CaseBatchReviewRequest {

    @NotEmpty(message = "案件ID列表不能为空")
    private List<Long> caseIds;

    @NotNull(message = "审核状态不能为空")
    private String reviewStatus;

    private String reviewOpinion;
}
