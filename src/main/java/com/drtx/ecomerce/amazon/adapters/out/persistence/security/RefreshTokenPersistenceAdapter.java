package com.drtx.ecomerce.amazon.adapters.out.persistence.security;

import com.drtx.ecomerce.amazon.core.model.security.RefreshToken;
import com.drtx.ecomerce.amazon.core.ports.out.security.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;
    private final RefreshTokenMapper mapper;

    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByUserEmail(String userEmail) {
        return jpaRepository.findByUserEmail(userEmail)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserEmail(String userEmail) {
        jpaRepository.deleteByUserEmail(userEmail);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        jpaRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void revokeByUserEmail(String userEmail) {
        jpaRepository.revokeByUserEmail(userEmail);
    }

    @Override
    @Transactional
    public int deleteExpiredTokens(Instant now) {
        return jpaRepository.deleteByExpiryDateBefore(now);
    }

    @Override
    @Transactional
    public int deleteRevokedTokens() {
        return jpaRepository.deleteByRevokedTrue();
    }
}

