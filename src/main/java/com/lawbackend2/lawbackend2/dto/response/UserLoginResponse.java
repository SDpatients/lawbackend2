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
public class UserLoginResponse {

    private Long userId;

    private String username;

    private String realName;

    private String accessToken;

    private String refreshToken;

    private List<String> permissions;
}
