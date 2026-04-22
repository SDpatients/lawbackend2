package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.dto.RecentCaseSearchRecord;
import com.lawbackend2.lawbackend2.entity.BankruptCase;
import com.lawbackend2.lawbackend2.exception.BusinessException;
import com.lawbackend2.lawbackend2.repository.BankruptCaseRepository;
import com.lawbackend2.lawbackend2.service.RecentCaseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecentCaseSearchServiceImpl implements RecentCaseSearchService {

    private static final String RECENT_CASE_SEARCH_KEY_PREFIX = "recent_case_search:user:";
    private static final int DEFAULT_LIMIT = 10;
    private static final Duration KEY_EXPIRATION = Duration.ofDays(30);

    private final RedisTemplate<String, Object> redisTemplate;
    private final BankruptCaseRepository bankruptCaseRepository;

    public RecentCaseSearchServiceImpl(RedisTemplate<String, Object> redisTemplate,
                                       BankruptCaseRepository bankruptCaseRepository) {
        this.redisTemplate = redisTemplate;
        this.bankruptCaseRepository = bankruptCaseRepository;
    }

    @Override
    public void recordCaseSearch(Long userId, Long caseId) {
        if (userId == null || caseId == null) {
            return;
        }

        String key = getKey(userId);

        try {
            BankruptCase bankruptCase = bankruptCaseRepository.findById(caseId)
                    .orElseThrow(() -> new BusinessException("案件不存在"));

            RecentCaseSearchRecord record = new RecentCaseSearchRecord(
                    bankruptCase.getId(),
                    bankruptCase.getCaseNumber(),
                    bankruptCase.getCaseName(),
                    bankruptCase.getCaseStatus(),
                    bankruptCase.getCaseProgress()
            );

            String memberKey = caseId.toString();

            redisTemplate.opsForZSet().remove(key, caseId);

            redisTemplate.opsForZSet().add(key, memberKey, System.currentTimeMillis());

            Set<Object> allMembers = redisTemplate.opsForZSet().reverseRange(key, 0, -1);
            if (allMembers != null && allMembers.size() > 50) {
                redisTemplate.opsForZSet().removeRange(key, 0, allMembers.size() - 51);
            }

            redisTemplate.expire(key, KEY_EXPIRATION);

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
            Set<Object> caseIds = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);

            if (caseIds == null || caseIds.isEmpty()) {
                return new ArrayList<>();
            }

            List<Long> ids = caseIds.stream()
                    .map(obj -> {
                        if (obj instanceof Long) {
                            return (Long) obj;
                        } else if (obj instanceof String) {
                            return Long.parseLong((String) obj);
                        } else if (obj instanceof Integer) {
                            return ((Integer) obj).longValue();
                        }
                        return null;
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toList());

            if (ids.isEmpty()) {
                return new ArrayList<>();
            }

            List<BankruptCase> cases = bankruptCaseRepository.findAllById(ids);

            List<RecentCaseSearchRecord> records = new ArrayList<>();
            for (Long caseId : ids) {
                cases.stream()
                        .filter(c -> c.getId().equals(caseId))
                        .findFirst()
                        .ifPresent(c -> records.add(new RecentCaseSearchRecord(
                                c.getId(),
                                c.getCaseNumber(),
                                c.getCaseName(),
                                c.getCaseStatus(),
                                c.getCaseProgress()
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
            redisTemplate.opsForZSet().remove(key, caseId);
            log.debug("移除用户最近查询记录, userId: {}, caseId: {}", userId, caseId);
        } catch (Exception e) {
            log.error("移除最近查询记录失败, userId: {}, caseId: {}, error: {}", userId, caseId, e.getMessage());
        }
    }

    private String getKey(Long userId) {
        return RECENT_CASE_SEARCH_KEY_PREFIX + userId;
    }
}