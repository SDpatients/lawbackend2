package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_dictionary_item", indexes = {
    @Index(name = "idx_item_category_id", columnList = "category_id"),
    @Index(name = "idx_item_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DictionaryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "item_code", nullable = false, length = 100)
    private String itemCode;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_value", length = 200)
    private String itemValue;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "create_time", updatable = false)
    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_time")
    @Builder.Default
    private LocalDateTime updateTime = LocalDateTime.now();

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
