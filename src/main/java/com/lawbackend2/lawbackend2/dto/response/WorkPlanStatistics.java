package com.lawbackend2.lawbackend2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkPlanStatistics {

    private Long totalPlans;
    private Long notStartedPlans;
    private Long inProgressPlans;
    private Long completedPlans;
    private Long delayedPlans;
    private Long cancelledPlans;
    private Map<String, Long> byPlanType;
}
