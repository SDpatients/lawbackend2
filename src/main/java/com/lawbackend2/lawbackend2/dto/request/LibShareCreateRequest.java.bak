package com.lawbackend2.lawbackend2.dto.request;

import javax.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibShareCreateRequest {

    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    private String sharePassword;

    @Builder.Default
    private String permissionType = "READ";

    private LocalDateTime expireTime;

    @Builder.Default
    private Integer maxAccessCount = 0;
}
