package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SystemFieldGroupResponse {
    private String group;
    private List<SystemFieldResponse> fields;
}
