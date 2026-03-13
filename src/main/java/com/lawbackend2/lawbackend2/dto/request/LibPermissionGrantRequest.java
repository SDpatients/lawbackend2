package com.lawbackend2.lawbackend2.dto.request;

import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibPermissionGrantRequest {

    @NotNull(message = "权限ID不能为空")
    private Long permissionId;

    @NotNull(message = "目标类型不能为空")
    private String targetType;

    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @Builder.Default
    private Boolean isInherit = true;
}
