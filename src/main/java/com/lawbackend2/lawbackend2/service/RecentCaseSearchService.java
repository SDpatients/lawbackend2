package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.dto.RecentCaseSearchRecord;

import java.util.List;

public interface RecentCaseSearchService {

    void recordCaseSearch(Long userId, Long caseId);

    List<RecentCaseSearchRecord> getRecentSearches(Long userId, int limit);

    void clearRecentSearches(Long userId);

    void removeRecentSearch(Long userId, Long caseId);
}