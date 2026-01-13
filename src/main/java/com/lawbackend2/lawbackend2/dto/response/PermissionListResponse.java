package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionListResponse {

    private Long total;

    private Integer page;

    private Integer size;

    private Integer totalPages;

    private List<PermissionResponse> permissions;
}
