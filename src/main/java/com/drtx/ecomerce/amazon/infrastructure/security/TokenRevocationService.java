package com.drtx.ecomerce.amazon.infrastructure.security;

import com.drtx.ecomerce.amazon.core.ports.out.security.RevokedTokenPort;
import com.drtx.ecomerce.amazon.core.ports.out.security.TokenRevocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRevocationService implements TokenRevocationPort {

    private final RevokedTokenPort revokedTokenPort;

    @Override
    public void invalidate(String token) {
        if(!revokedTokenPort.exists(token)) revokedTokenPort.save(token);
    }

    @Override
    public boolean isInvalidated(String token) {
        return revokedTokenPort.exists(token);
    }

    @Override
    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredTokens() {
        revokedTokenPort.deleteExpiredTokens();
    }
}
