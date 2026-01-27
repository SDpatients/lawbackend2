package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ExpenseReimbursementUpdateRequest {

    @NotNull(message = "报销单ID不能为空")
    private Long id;

    @NotNull(message = "案件ID不能为空")
    private Long caseId;

    @NotNull(message = "银行账户ID不能为空")
    private Long fundAccountId;

    @NotNull(message = "报销日期不能为空")
    private java.time.LocalDate reimbursementDate;

    @Size(max = 500, message = "报销说明不能超过500个字符")
    private String description;
}
