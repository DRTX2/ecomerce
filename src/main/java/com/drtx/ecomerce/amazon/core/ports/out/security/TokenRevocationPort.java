package com.drtx.ecomerce.amazon.core.ports.out.security;

public interface TokenRevocationPort {
    void invalidate(String token);
    boolean isInvalidated(String token);
    void deleteExpiredTokens();
}
