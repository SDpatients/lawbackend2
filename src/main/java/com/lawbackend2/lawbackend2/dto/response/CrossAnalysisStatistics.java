package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrossAnalysisStatistics {
    private String type;
    private Map<String, Map<String, Long>> crossData;
    private Long totalCount;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> progressDistribution;
}
