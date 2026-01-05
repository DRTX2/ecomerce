package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.RevokedTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@Primary
@RequiredArgsConstructor
public class RedisTokenRevocationAdapter implements RevokedTokenPort {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "revoked_token:";

    @Override
    public void save(String token) {
        // We set the value to "revoked" and TTL to 24 hours (86400000ms) or slightly
        // more/less
        // Ideally we should extract expiration from token, but token parsing here might
        // duplicate logic.
        // For simplicity, we use a fixed TTL covering the max token lifetime (e.g.
        // 24h).
        redisTemplate.opsForValue().set(KEY_PREFIX + token, "revoked", 24, TimeUnit.HOURS);
    }

    @Override
    public boolean exists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + token));
    }

    @Override
    public void deleteExpiredTokens() {
        // Redis handles expiration automatically. No-op.
    }
}
