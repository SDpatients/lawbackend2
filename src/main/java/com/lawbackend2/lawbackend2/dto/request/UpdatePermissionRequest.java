package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdatePermissionRequest {

    @NotBlank(message = "权限代码不能为空")
    @Size(max = 100, message = "权限代码长度不能超过100")
    private String permCode;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称长度不能超过100")
    private String permName;

    @NotBlank(message = "权限类型不能为空")
    @Size(max = 1, message = "权限类型长度不能超过1")
    private String permType;

    private Long parentId;

    @Size(max = 200, message = "路径长度不能超过200")
    private String path;

    @Size(max = 200, message = "组件长度不能超过200")
    private String component;

    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    private Integer sortOrder;

    private String status;

    private Character isExternal;
}
