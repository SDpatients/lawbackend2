package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseAccessibleUserResponse {
    private Long userId;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String accessType;
    private String accessRole;
    private LocalDateTime accessTime;
}
