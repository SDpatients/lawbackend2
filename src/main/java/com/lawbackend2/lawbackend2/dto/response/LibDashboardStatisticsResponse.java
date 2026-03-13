package com.lawbackend2.lawbackend2.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibDashboardStatisticsResponse {

    private Long totalDocuments;
    private Long totalSize;
    private Long weeklyUploads;
    private Long totalViews;
    private Map<String, Long> typeDistribution;
    private Map<String, Long> sizeDistribution;
    private List<MonthlyTrend> monthlyTrend;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrend {
        private String month;
        private Long uploads;
        private Long views;
    }
}
