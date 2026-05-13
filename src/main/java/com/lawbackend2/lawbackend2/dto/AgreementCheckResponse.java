package com.lawbackend2.lawbackend2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgreementCheckResponse {

    private Map<String, Boolean> agreements;
    private Boolean allAgreed;
    private LocalDateTime checkTime;
}