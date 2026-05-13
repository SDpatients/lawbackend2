package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.config.AppProperties;
import com.lawbackend2.lawbackend2.dto.RecentCaseSearchRecord;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.RecentCaseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecentCaseSearchServiceImpl implements RecentCaseSearchService {

    private static final String RECENT_CASE_SEARCH_KEY_PREFIX = "recent_case_search:user:";
    private static final int DEFAULT_LIMIT = 10;

    private final RedisTemplate<String, Object> redisTemplate;
    private final BankruptCaseRepository bankruptCaseRepository;
    private final AppProperties appProperties;

    public RecentCaseSearchServiceImpl(RedisTemplate<String, Object> redisTemplate,
                                       BankruptCaseRepository bankruptCaseRepository,
                                       AppProperties appProperties) {
        this.redisTemplate = redisTemplate;
        this.bankruptCaseRepository = bankruptCaseRepository;
        this.appProperties = appProperties;
    }

    private int getMaxRecords() {
        return appProperties.getCache().getRecentCase().getMaxRecords();
    }

    private Duration getKeyExpiration() {
        return Duration.ofDays(appProperties.getCache().getRecentCase().getExpireDays());
    }

    @Override
    public void recordCaseSearch(Long userId, Long caseId) {
        if (userId == null || caseId == null) {
            return;
        }

        String key = getKey(userId);

        try {
            if (!bankruptCaseRepository.existsById(caseId)) {
                log.warn("案件不存在, caseId: {}", caseId);
                return;
            }

            String memberKey = caseId.toString();

            redisTemplate.opsForZSet().remove(key, memberKey);

            redisTemplate.opsForZSet().add(key, memberKey, System.currentTimeMillis());

            Long size = redisTemplate.opsForZSet().size(key);
            int maxRecords = getMaxRecords();
            if (size != null && size > maxRecords) {
                redisTemplate.opsForZSet().removeRange(key, 0, size - maxRecords - 1);
            }

            redisTemplate.expire(key, getKeyExpiration());

            log.debug("记录用户最近查询案件, userId: {}, caseId: {}", userId, caseId);
        } catch (Exception e) {
            log.error("记录最近查询案件失败, userId: {}, caseId: {}, error: {}", userId, caseId, e.getMessage());
        }
    }

    @Override
    public List<RecentCaseSearchRecord> getRecentSearches(Long userId, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }

        String key = getKey(userId);
        if (limit <= 0) {
            limit = DEFAULT_LIMIT;
        }

        try {
            Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet()
                    .reverseRangeWithScores(key, 0, limit - 1);

            if (tuples == null || tuples.isEmpty()) {
                return new ArrayList<>();
            }

            List<Long> ids = tuples.stream()
                    .map(tuple -> {
                        Object value = tuple.getValue();
                        if (value instanceof Long) {
                            return (Long) value;
                        } else if (value instanceof String) {
                            return Long.parseLong((String) value);
                        } else if (value instanceof Integer) {
                            return ((Integer) value).longValue();
                        }
                        return null;
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toList());

            if (ids.isEmpty()) {
                return new ArrayList<>();
            }

            Map<Long, Double> caseIdToScoreMap = tuples.stream()
                    .filter(tuple -> tuple.getValue() != null && tuple.getScore() != null)
                    .collect(Collectors.toMap(
                            tuple -> {
                                Object value = tuple.getValue();
                                if (value instanceof Long) {
                                    return (Long) value;
                                } else if (value instanceof String) {
                                    return Long.parseLong((String) value);
                                } else if (value instanceof Integer) {
                                    return ((Integer) value).longValue();
                                }
                                return -1L;
                            },
                            tuple -> tuple.getScore(),
                            (existing, replacement) -> existing
                    ));

            List<BankruptCase> cases = bankruptCaseRepository.findAllById(ids);

            List<RecentCaseSearchRecord> records = new ArrayList<>();
            for (Long caseId : ids) {
                Double score = caseIdToScoreMap.get(caseId);
                if (score == null) {
                    continue;
                }
                Long timestamp = score.longValue();

                cases.stream()
                        .filter(c -> c.getId().equals(caseId))
                        .findFirst()
                        .ifPresent(c -> records.add(new RecentCaseSearchRecord(
                                c.getId(),
                                c.getCaseNumber(),
                                c.getCaseName(),
                                c.getCaseStatus(),
                                c.getCaseProgress(),
                                timestamp
                        )));
            }

            return records;
        } catch (Exception e) {
            log.error("获取最近查询案件失败, userId: {}, error: {}", userId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void clearRecentSearches(Long userId) {
        if (userId == null) {
            return;
        }

        String key = getKey(userId);
        try {
            redisTemplate.delete(key);
            log.debug("清除用户最近查询记录, userId: {}", userId);
        } catch (Exception e) {
            log.error("清除最近查询记录失败, userId: {}, error: {}", userId, e.getMessage());
        }
    }

    @Override
    public void removeRecentSearch(Long userId, Long caseId) {
        if (userId == null || caseId == null) {
            return;
        }

        String key = getKey(userId);
        try {
            redisTemplate.opsForZSet().remove(key, caseId.toString());
            log.debug("移除用户最近查询记录, userId: {}, caseId: {}", userId, caseId);
        } catch (Exception e) {
            log.error("移除最近查询记录失败, userId: {}, caseId: {}, error: {}", userId, caseId, e.getMessage());
        }
    }

    private String getKey(Long userId) {
        return RECENT_CASE_SEARCH_KEY_PREFIX + userId;
    }
}