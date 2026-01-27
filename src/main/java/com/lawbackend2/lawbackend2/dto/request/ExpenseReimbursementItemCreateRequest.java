package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExpenseReimbursementItemCreateRequest {

    @NotNull(message = "报销单ID不能为空")
    private Long reimbursementId;

    @NotNull(message = "费用名称不能为空")
    @javax.validation.constraints.NotBlank(message = "费用名称不能为空")
    @javax.validation.constraints.Size(max = 100, message = "费用名称不能超过100个字符")
    private String itemName;

    @NotNull(message = "费用金额不能为空")
    @javax.validation.constraints.DecimalMin(value = "0.01", message = "费用金额必须大于0")
    @javax.validation.constraints.Digits(integer = 16, fraction = 2, message = "金额格式不正确")
    private java.math.BigDecimal itemAmount;

    @javax.validation.constraints.Size(max = 500, message = "费用说明不能超过500个字符")
    private String itemDescription;
}
