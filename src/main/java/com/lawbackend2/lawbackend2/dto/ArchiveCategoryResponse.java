package com.lawbackend2.lawbackend2.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArchiveCategoryResponse {
    private Long id;

    private String categoryCode;

    private String categoryName;

    private Long parentId;

    private Integer level;

    private Integer sortOrder;

    private Boolean isRequired;

    private String status;

    private String description;

    private List<ArchiveCategoryResponse> children;
}
