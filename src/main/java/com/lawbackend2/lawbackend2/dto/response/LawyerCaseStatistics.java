package com.lawbackend2.lawbackend2.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LawyerCaseStatistics {

    private Long userId;

    private String realName;

    private String username;

    private Long totalCaseCount;

    private Long leaderCaseCount;

    private Long adminCaseCount;

    private Integer year;
}
