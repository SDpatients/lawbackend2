package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionTreeResponse {

    private Long id;

    private String permCode;

    private String permName;

    private String permType;

    private Long parentId;

    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    private String status;

    private List<PermissionTreeResponse> children;
}
