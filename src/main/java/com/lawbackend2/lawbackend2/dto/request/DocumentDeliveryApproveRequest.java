package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DocumentDeliveryApproveRequest {

    @NotNull(message = "送达记录ID不能为空")
    private Long deliveryId;

    @NotBlank(message = "审批结果不能为空")
    private String approvalResult;

    private String remark;
}
