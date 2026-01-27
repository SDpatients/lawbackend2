package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpenseReimbursementItemResponse {

    private Long id;
    private Long reimbursementId;
    private String itemName;
    private BigDecimal itemAmount;
    private String itemDescription;
    private Integer sortOrder;
}
