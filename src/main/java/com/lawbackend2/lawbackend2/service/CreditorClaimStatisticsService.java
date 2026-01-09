package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsRequest;
import com.lawbackend2.lawbackend2.dto.CreditorClaimStatisticsResponse;

public interface CreditorClaimStatisticsService {
    CreditorClaimStatisticsResponse getCreditorClaimStatistics(CreditorClaimStatisticsRequest request);
}
