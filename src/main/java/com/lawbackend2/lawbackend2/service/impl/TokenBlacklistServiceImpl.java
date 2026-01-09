package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${token.blacklist.prefix}")
    private String blacklistPrefix;

    @Value("${token.blacklist.ttl}")
    private long defaultTtl;

    @Override
    public void addToBlacklist(String token) {
        addToBlacklist(token, defaultTtl);
    }

    @Override
    public void addToBlacklist(String token, long ttl) {
        String key = blacklistPrefix + token;
        redisTemplate.opsForValue().set(key, System.currentTimeMillis(), ttl, TimeUnit.MILLISECONDS);
        log.info("Token已添加到黑名单 - Token: {}, TTL: {}ms", token.substring(0, Math.min(20, token.length())) + "...", ttl);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = blacklistPrefix + token;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    @Override
    public void removeFromBlacklist(String token) {
        String key = blacklistPrefix + token;
        redisTemplate.delete(key);
        log.info("Token已从黑名单移除 - Token: {}", token.substring(0, Math.min(20, token.length())) + "...");
    }

    @Override
    public void clearBlacklist() {
        Set<String> keys = redisTemplate.keys(blacklistPrefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("已清空Token黑名单，共删除 {} 个Token", keys.size());
        }
    }
}
