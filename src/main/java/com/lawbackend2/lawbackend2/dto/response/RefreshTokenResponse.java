package com.lawbackend2.lawbackend2.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RefreshTokenResponse {
    private Long userId;
    private String username;
    private String realName;
    private String accessToken;
    private String refreshToken;
    private List<String> permissions;
}
