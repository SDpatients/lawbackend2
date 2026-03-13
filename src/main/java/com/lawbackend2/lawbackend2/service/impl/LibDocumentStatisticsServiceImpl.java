package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.response.LibDashboardStatisticsResponse;
import com.lawbackend2.lawbackend2.repository.LibDocumentOperationLogRepository;
import com.lawbackend2.lawbackend2.repository.LibDocumentRepository;
import com.lawbackend2.lawbackend2.service.LibDocumentStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibDocumentStatisticsServiceImpl implements LibDocumentStatisticsService {

    private final LibDocumentRepository documentRepository;
    private final LibDocumentOperationLogRepository operationLogRepository;

    @Cacheable(value = "libDashboard", key = "'statistics'")
    @Override
    public LibDashboardStatisticsResponse getDashboardStatistics() {
        Long totalDocuments = documentRepository.countAllDocuments();
        Long totalSize = documentRepository.sumTotalFileSize();
        
        LocalDateTime weekStart = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
        Long weeklyUploads = documentRepository.countDocumentsSince(weekStart);
        
        Long totalViews = documentRepository.sumTotalViewCount();
        
        Map<String, Object[]> typeStats = getTypeStats();
        Map<String, Long> typeDistribution = new HashMap<>();
        Map<String, Long> sizeDistribution = new HashMap<>();
        for (Map.Entry<String, Object[]> entry : typeStats.entrySet()) {
            typeDistribution.put(entry.getKey(), (Long) entry.getValue()[0]);
            sizeDistribution.put(entry.getKey(), (Long) entry.getValue()[1]);
        }
        
        List<LibDashboardStatisticsResponse.MonthlyTrend> monthlyTrend = getMonthlyTrend();

        return LibDashboardStatisticsResponse.builder()
                .totalDocuments(totalDocuments != null ? totalDocuments : 0L)
                .totalSize(totalSize != null ? totalSize : 0L)
                .weeklyUploads(weeklyUploads != null ? weeklyUploads : 0L)
                .totalViews(totalViews != null ? totalViews : 0L)
                .typeDistribution(typeDistribution)
                .sizeDistribution(sizeDistribution)
                .monthlyTrend(monthlyTrend)
                .build();
    }

    private Map<String, Object[]> getTypeStats() {
        Map<String, Object[]> result = new HashMap<>();
        List<Object[]> stats = documentRepository.getDocumentTypeStats();
        for (Object[] row : stats) {
            String type = (String) row[0];
            Long count = (Long) row[1];
            Long size = (Long) row[2];
            String key = type != null ? type : "OTHER";
            result.put(key, new Object[]{count != null ? count : 0L, size != null ? size : 0L});
        }
        return result;
    }

    private List<LibDashboardStatisticsResponse.MonthlyTrend> getMonthlyTrend() {
        Map<String, Long> uploadTrend = new LinkedHashMap<>();
        Map<String, Long> viewTrend = new LinkedHashMap<>();
        
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        
        List<Object[]> uploadResults = documentRepository.countDocumentsByMonth(threeMonthsAgo);
        for (Object[] row : uploadResults) {
            String month = (String) row[0];
            Long count = (Long) row[1];
            if (month != null) {
                uploadTrend.put(month, count != null ? count : 0L);
            }
        }
        
        List<Object[]> viewResults = operationLogRepository.countViewsByMonth(threeMonthsAgo);
        for (Object[] row : viewResults) {
            String month = (String) row[0];
            Long count = (Long) row[1];
            if (month != null) {
                viewTrend.put(month, count != null ? count : 0L);
            }
        }
        
        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(uploadTrend.keySet());
        allMonths.addAll(viewTrend.keySet());
        
        List<LibDashboardStatisticsResponse.MonthlyTrend> trends = new ArrayList<>();
        for (String month : allMonths) {
            trends.add(LibDashboardStatisticsResponse.MonthlyTrend.builder()
                    .month(month)
                    .uploads(uploadTrend.getOrDefault(month, 0L))
                    .views(viewTrend.getOrDefault(month, 0L))
                    .build());
        }
        
        return trends;
    }
}
