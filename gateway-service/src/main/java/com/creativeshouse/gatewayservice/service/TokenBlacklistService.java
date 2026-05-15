package com.creativeshouse.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_PREFIX = "BLACKLIST:TOKEN:";

    public void blacklistToken(String token, long expiryInMillis) {
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "BLACKLISTED", expiryInMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("Failed to blacklist token in Redis: {}. Continuing without Redis.", e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Redis is unreachable. Skipping token blacklist check: {}", e.getMessage());
            return false;
        }
    }
}
