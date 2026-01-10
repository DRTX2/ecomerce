package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.RevokedTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class RevokedTokenPersistenceAdapter implements RevokedTokenPort {

    private final RevokedTokenRepository repository;

    @Override
    @Transactional
    public void save(String token) {
        if (!repository.existsByToken(token)) {
            Instant expiresAt = Instant.now().plusMillis(86400000); // Default 24h
            repository.save(new RevokedToken(token, expiresAt));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String token) {
        return repository.existsByToken(token);
    }

    @Override
    @Transactional
    public int deleteRevokedBefore(Instant before) {
        return repository.deleteByRevokedAtBefore(before);
    }

}

