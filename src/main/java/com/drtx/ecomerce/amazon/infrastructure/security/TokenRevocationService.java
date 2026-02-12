package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.RevokedTokenPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRevocationService implements TokenRevocationPort {

    private final RevokedTokenPort revokedTokenPort;

    @Override
    public void invalidate(String token) {
        if (!revokedTokenPort.exists(token)) {
            revokedTokenPort.save(token);
            log.debug("Token invalidated successfully");
        }
    }

    @Override
    public boolean isInvalidated(String token) {
        return revokedTokenPort.exists(token);
    }

    @Override
    public int deleteTokensRevokedBefore(Instant before) {
        int deleted = revokedTokenPort.deleteRevokedBefore(before);
        log.info("Deleted {} revoked tokens older than {}", deleted, before);
        return deleted;
    }
}
