package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainException;
import com.drtx.ecomerce.amazon.core.model.security.RefreshToken;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.out.security.RefreshTokenRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public RefreshToken createRefreshToken(User user) {
        log.debug("Creating refresh token for user: {}", user.getEmail());

        // Revocar tokens anteriores del usuario
        refreshTokenRepository.findByUserEmail(user.getEmail())
                .ifPresent(existingToken -> {
                    log.debug("Revoking previous refresh token for user: {}", user.getEmail());
                    refreshTokenRepository.deleteByToken(existingToken.getToken());
                });

        String tokenString = tokenProvider.generateRefreshToken(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenString)
                .userEmail(user.getEmail())
                .expiryDate(Instant.now().plusMillis(tokenProvider.getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for user: {}", user.getEmail());

        return saved;
    }

    public RefreshToken verifyAndGetRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found: {}...", token.substring(0, Math.min(20, token.length())));
                    return new DomainException("Refresh token not found");
                });

        if (!refreshToken.isValid()) {
            log.warn("Invalid/expired refresh token for user: {}", refreshToken.getUserEmail());
            refreshTokenRepository.deleteByToken(token);
            throw new DomainException("Refresh token is invalid or expired");
        }

        log.debug("Refresh token validated for user: {}", refreshToken.getUserEmail());
        return refreshToken;
    }

    public void revokeRefreshToken(String userEmail) {
        log.info("Revoking refresh token for user: {}", userEmail);
        refreshTokenRepository.revokeByUserEmail(userEmail);
    }

    public void deleteRefreshToken(String token) {
        log.debug("Deleting refresh token");
        refreshTokenRepository.deleteByToken(token);
    }
}

