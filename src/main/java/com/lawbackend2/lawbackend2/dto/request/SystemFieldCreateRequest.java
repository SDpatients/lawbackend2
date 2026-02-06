package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SystemFieldCreateRequest {
    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotBlank(message = "字段标签不能为空")
    private String label;

    @NotBlank(message = "字段值不能为空")
    private String value;

    private Integer sortOrder;

    private String description;
}
