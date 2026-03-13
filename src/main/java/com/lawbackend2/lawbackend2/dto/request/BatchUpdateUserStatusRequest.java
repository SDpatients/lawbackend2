package com.lawbackend2.lawbackend2.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchUpdateUserStatusRequest {

    @NotEmpty(message = "用户ID列表不能为空")
    private List<Long> userIds;

    @NotBlank(message = "用户状态不能为空")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|LOCKED)$", message = "用户状态不正确")
    private String status;
}
