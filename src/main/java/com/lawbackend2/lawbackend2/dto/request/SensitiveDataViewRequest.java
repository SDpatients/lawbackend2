package com.lawbackend2.lawbackend2.dto.request;

import com.lawbackend2.lawbackend2.enums.SensitiveDataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "敏感数据完整查看请求")
public class SensitiveDataViewRequest {

    @NotNull(message = "数据类型不能为空")
    @Schema(description = "数据类型")
    private SensitiveDataType dataType;

    @NotNull(message = "关联ID不能为空")
    @Schema(description = "关联ID（用户ID/债权人ID/银行账户ID等）")
    private Long id;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "当前登录用户的密码")
    private String password;
}