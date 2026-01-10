package com.drtx.ecomerce.amazon.core.ports.out.security;

import com.drtx.ecomerce.amazon.core.model.security.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserEmail(String userEmail);

    void deleteByUserEmail(String userEmail);

    void deleteByToken(String token);

    void revokeByUserEmail(String userEmail);

    /**
     * Elimina todos los tokens expirados (expiry_date < now).
     * @return cantidad de tokens eliminados
     */
    int deleteExpiredTokens(Instant now);

    /**
     * Elimina todos los tokens revocados.
     * @return cantidad de tokens eliminados
     */
    int deleteRevokedTokens();
}

