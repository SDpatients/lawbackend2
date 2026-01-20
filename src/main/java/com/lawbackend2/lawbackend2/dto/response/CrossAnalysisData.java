package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrossAnalysisData {
    private String status;
    private String progress;
    private Long count;
    private Double percentage;
}