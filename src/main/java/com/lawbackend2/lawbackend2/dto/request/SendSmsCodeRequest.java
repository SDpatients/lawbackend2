package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSmsCodeRequest {

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotBlank(message = "短信类型不能为空")
    @Pattern(regexp = "^[1-3]$", message = "短信类型不正确")
    private String smsType;
}
