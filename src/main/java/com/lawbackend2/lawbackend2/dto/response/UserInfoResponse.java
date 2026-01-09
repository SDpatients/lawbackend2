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
public class UserInfoResponse {

    private Long id;

    private String username;

    private String realName;

    private String mobile;

    private String email;

    private String phone;

    private String status;

    private List<String> roles;

    private List<String> permissions;
}
