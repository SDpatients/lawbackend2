package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class WorkPlanStatistics {

    private Long totalPlans;
    private Long notStartedPlans;
    private Long inProgressPlans;
    private Long completedPlans;
    private Long delayedPlans;
    private Long cancelledPlans;
    private Map<String, Long> byPlanType;
}
