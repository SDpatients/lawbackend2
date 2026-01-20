package com.lawbackend2.lawbackend2.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_archive_category", indexes = {
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_level", columnList = "level"),
    @Index(name = "idx_sort_order", columnList = "sort_order"),
    @Index(name = "idx_status", columnList = "status")
})
public class ArchiveCategory extends BaseEntity {

    @Column(name = "category_code", length = 100, nullable = false, unique = true)
    private String categoryCode;

    @Column(name = "category_name", length = 200, nullable = false)
    private String categoryName;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "level", nullable = false)
    private Integer level = 1;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Transient
    private List<ArchiveCategory> children = new ArrayList<>();
}
