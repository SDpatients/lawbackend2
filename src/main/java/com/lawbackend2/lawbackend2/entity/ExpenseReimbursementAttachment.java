package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_expense_reimbursement_attachment", indexes = {
    @Index(name = "idx_reimbursement_id", columnList = "reimbursement_id")
})
public class ExpenseReimbursementAttachment extends BaseEntity {

    @Column(name = "reimbursement_id", nullable = false)
    private Long reimbursementId;

    @Column(name = "file_name", length = 255, nullable = false)
    private String fileName;

    @Column(name = "file_path", length = 1000, nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "upload_time")
    private java.time.LocalDateTime uploadTime;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
