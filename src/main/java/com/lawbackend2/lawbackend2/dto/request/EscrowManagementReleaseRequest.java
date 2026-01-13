package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EscrowManagementReleaseRequest {

    @NotBlank(message = "释放状态不能为空")
    private String releaseStatus;

    private BigDecimal releasedAmount;

    private Long releaseAccountId;

    private String releaseVoucher;

    private LocalDateTime releaseDate;

    private Boolean isConditionMet;

    private LocalDateTime conditionMetDate;
}