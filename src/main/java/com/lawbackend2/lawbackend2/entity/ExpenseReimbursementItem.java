package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_expense_reimbursement_item", indexes = {
    @Index(name = "idx_reimbursement_id", columnList = "reimbursement_id")
})
public class ExpenseReimbursementItem extends BaseEntity {

    @Column(name = "reimbursement_id", nullable = false)
    private Long reimbursementId;

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName;

    @Column(name = "item_amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal itemAmount;

    @Column(name = "item_description", length = 500)
    private String itemDescription;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
