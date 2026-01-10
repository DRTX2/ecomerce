package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.RevokedTokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class RedisTokenRevocationAdapter implements RevokedTokenPort {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "revoked_token:";

    @Override
    public void save(String token) {
        // TTL de 24 horas - Redis maneja la expiración automáticamente
        redisTemplate.opsForValue().set(KEY_PREFIX + token, "revoked", 24, TimeUnit.HOURS);
        log.debug("Token revoked in Redis");
    }

    @Override
    public boolean exists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + token));
    }

    @Override
    public int deleteRevokedBefore(Instant before) {
        // Redis maneja la expiración automáticamente con TTL
        // No es necesario eliminar manualmente, retornamos 0
        log.debug("Redis handles token expiration automatically via TTL");
        return 0;
    }
}
