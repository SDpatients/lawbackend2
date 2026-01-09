package com.lawbackend2.lawbackend2.service.impl;

import com.lawbackend2.lawbackend2.entity.Token;
import com.lawbackend2.lawbackend2.repository.TokenRepository;
import com.lawbackend2.lawbackend2.service.TokenCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TokenCleanupServiceImpl implements TokenCleanupService {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("开始清理过期Token...");

        LocalDateTime now = LocalDateTime.now();
        List<Token> expiredTokens = tokenRepository.findByExpireTimeBeforeAndIsDeletedFalse(now);

        if (!expiredTokens.isEmpty()) {
            int count = expiredTokens.size();
            for (Token token : expiredTokens) {
                token.setIsDeleted(true);
                tokenRepository.save(token);
            }
            log.info("过期Token清理完成，共清理 {} 个Token", count);
        } else {
            log.info("没有发现过期的Token");
        }
    }

    @Override
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupInactiveTokens() {
        log.info("开始清理失效Token...");

        List<Token> inactiveTokens = tokenRepository.findByStatusAndIsDeletedFalse("INACTIVE");

        if (!inactiveTokens.isEmpty()) {
            int count = inactiveTokens.size();
            for (Token token : inactiveTokens) {
                LocalDateTime revokeTime = token.getRevokeTime();
                if (revokeTime != null && revokeTime.isBefore(LocalDateTime.now().minusDays(7))) {
                    token.setIsDeleted(true);
                    tokenRepository.save(token);
                }
            }
            log.info("失效Token清理完成，共清理 {} 个Token", count);
        } else {
            log.info("没有发现失效的Token");
        }
    }
}
