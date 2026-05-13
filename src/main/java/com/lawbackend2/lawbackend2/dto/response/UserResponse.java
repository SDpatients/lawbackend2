package com.lawbackend2.lawbackend2.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lawbackend2.lawbackend2.annotation.Mask;
import com.lawbackend2.lawbackend2.annotation.MaskType;
import com.lawbackend2.lawbackend2.util.MaskSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String realName;

    @Mask(MaskType.PHONE)
    @JsonSerialize(using = MaskSerializer.class)
    private String mobile;

    private String email;

    @Mask(MaskType.PHONE)
    @JsonSerialize(using = MaskSerializer.class)
    private String phone;

    private Character isValid;
    private String status;
    private Character loginType;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Integer loginCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
