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
public class UserListResponse {

    private Long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private List<UserResponse> users;
}
