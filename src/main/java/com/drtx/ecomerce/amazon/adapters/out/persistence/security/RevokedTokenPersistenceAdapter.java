package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.RevokedTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class RevokedTokenPersistenceAdapter implements RevokedTokenPort {

    private final RevokedTokenRepository repository;

    @Override
    public void save(String token) {
        if (!repository.existsByToken(token)) {
            Instant expiresAt = extractExpirationFromToken(token);
            repository.save(new RevokedToken(token, expiresAt));
        }
    }

    private Instant extractExpirationFromToken(String token) {
        try {
            // You'll need to inject JwtService or extract expiration here
            // For now, using a default expiration based on your config
            return Instant.now().plusMillis(86400000);
        } catch (Exception e) {
            return Instant.now().plusMillis(86400000); // Default to 1 day if expiration cannot be extracted
        }
    }

    @Override
    public boolean exists(String token) {
        return repository.existsByToken(token);
    }

    @Override
    public void deleteExpiredTokens() {
        repository.deleteByExpiresAtBefore(Instant.now());
    }

}

