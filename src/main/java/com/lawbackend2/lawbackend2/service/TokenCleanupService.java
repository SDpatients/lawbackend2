package com.lawbackend2.lawbackend2.service;

public interface TokenCleanupService {
    void cleanupExpiredTokens();

    void cleanupInactiveTokens();
}
