package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPatchRequest {

    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;

    @Size(min = 8, max = 20, message = "密码长度必须在8-20之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "密码必须包含大小写字母、数字和特殊字符")
    private String password;

    @Size(max = 50, message = "真实姓名长度不能超过50")
    private String realName;

    private String mobile;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    private String status;
}
