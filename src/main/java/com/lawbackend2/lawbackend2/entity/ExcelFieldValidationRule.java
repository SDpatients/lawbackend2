package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_excel_field_validation_rule")
public class ExcelFieldValidationRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "field_name", length = 50, nullable = false)
    private String fieldName;
    
    @Column(name = "rule_type", length = 20, nullable = false)
    private String ruleType;
    
    @Column(name = "rule_value", length = 500)
    private String ruleValue;
    
    @Column(name = "error_message", length = 200, nullable = false)
    private String errorMessage;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "priority")
    private Integer priority;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
