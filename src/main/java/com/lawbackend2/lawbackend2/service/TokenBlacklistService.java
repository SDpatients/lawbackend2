package com.lawbackend2.lawbackend2.service;

public interface TokenBlacklistService {
    void addToBlacklist(String token);

    void addToBlacklist(String token, long ttl);

    boolean isTokenBlacklisted(String token);

    void removeFromBlacklist(String token);

    void clearBlacklist();
}
