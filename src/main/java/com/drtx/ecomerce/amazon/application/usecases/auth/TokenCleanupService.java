package com.drtx.ecomerce.amazon.application.usecases.auth;

import com.drtx.ecomerce.amazon.core.ports.out.security.RefreshTokenRepositoryPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "security.token-cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class TokenCleanupService {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenRevocationPort tokenRevocationPort;

    @Value("${security.token-cleanup.revoked-token-retention-days:30}")
    private int revokedTokenRetentionDays;

    /**
     * clean tokens, by default at 2AM(development), each 6 hours in production.
     */
    @Scheduled(cron = "${security.token-cleanup.cron:0 0 2 * * *}")
    @Transactional
    public void scheduledCleanup() {
        log.info("Starting scheduled token cleanup job...");
        cleanup();
    }

    /**
     * manually executes the cleanup.
     * Useful for testing or execution from an admin endpoint.
     */
    @Transactional
    public CleanupResult cleanup() {
        long startTime = System.currentTimeMillis();
        Instant now = Instant.now();

        log.info("Token cleanup started at {}", now);

        // 1. delete expired refresh tokens
        int expiredRefreshTokensDeleted = cleanupExpiredRefreshTokens(now);

        // 2. delete revoked refresh tokens
        int revokedRefreshTokensDeleted = cleanupRevokedRefreshTokens();

        // 3. Delete old revoked access tokens beyond retention period
        Instant retentionCutoff = now.minus(revokedTokenRetentionDays, ChronoUnit.DAYS);
        int oldRevokedTokensDeleted = cleanupOldRevokedTokens(retentionCutoff);

        long duration = System.currentTimeMillis() - startTime;

        CleanupResult result = new CleanupResult(
            expiredRefreshTokensDeleted,
            revokedRefreshTokensDeleted,
            oldRevokedTokensDeleted,
            duration
        );

        log.info("Token cleanup completed in {} ms. Results: {}", duration, result);

        return result;
    }

    private int cleanupExpiredRefreshTokens(Instant now) {
        try {
            int deleted = refreshTokenRepository.deleteExpiredTokens(now);
            if (deleted > 0) {
                log.info("Deleted {} expired refresh tokens", deleted);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting expired refresh tokens", e);
            return 0;
        }
    }

    private int cleanupRevokedRefreshTokens() {
        try {
            int deleted = refreshTokenRepository.deleteRevokedTokens();
            if (deleted > 0) {
                log.info("Deleted {} revoked refresh tokens", deleted);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting revoked refresh tokens", e);
            return 0;
        }
    }

    private int cleanupOldRevokedTokens(Instant before) {
        try {
            int deleted = tokenRevocationPort.deleteTokensRevokedBefore(before);
            if (deleted > 0) {
                log.info("Deleted {} revoked access tokens older than {} days",
                    deleted, revokedTokenRetentionDays);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting old revoked tokens", e);
            return 0;
        }
    }

    public record CleanupResult(
        int expiredRefreshTokensDeleted,
        int revokedRefreshTokensDeleted,
        int oldRevokedAccessTokensDeleted,
        long durationMs
    ) {
        public int totalDeleted() {
            return expiredRefreshTokensDeleted + revokedRefreshTokensDeleted + oldRevokedAccessTokensDeleted;
        }

        @Override
        public String toString() {
            return String.format(
                "CleanupResult{expired=%d, revoked=%d, oldRevoked=%d, total=%d, duration=%dms}",
                expiredRefreshTokensDeleted,
                revokedRefreshTokensDeleted,
                oldRevokedAccessTokensDeleted,
                totalDeleted(),
                durationMs
            );
        }
    }
}