package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CaseStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CaseStatisticsResponse;

public interface CaseStatisticsService {
    CaseStatisticsResponse getCaseStatistics(CaseStatisticsRequest request);
}
