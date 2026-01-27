package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ExpenseReimbursementCreateRequest {

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotNull(message = "银行账户ID不能为空")
    private Long fundAccountId;

    @NotNull(message = "报销日期不能为空")
    private LocalDate reimbursementDate;

    @Size(max = 500, message = "报销说明不能超过500个字符")
    private String description;

    @NotEmpty(message = "报销明细不能为空")
    @Size(min = 1, message = "至少需要一条报销明细")
    private List<ExpenseReimbursementItemRequest> items;

    @Data
    public static class ExpenseReimbursementItemRequest {

        @NotBlank(message = "费用名称不能为空")
        @Size(max = 100, message = "费用名称不能超过100个字符")
        private String itemName;

        @NotNull(message = "费用金额不能为空")
        @DecimalMin(value = "0.01", message = "费用金额必须大于0")
        @Digits(integer = 16, fraction = 2, message = "金额格式不正确")
        private BigDecimal itemAmount;

        @Size(max = 500, message = "费用说明不能超过500个字符")
        private String itemDescription;
    }
}
