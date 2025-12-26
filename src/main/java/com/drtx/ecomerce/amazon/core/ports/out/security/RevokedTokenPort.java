package com.drtx.ecomerce.amazon.core.ports.out.security;

public interface RevokedTokenPort {
    void save(String token);
    boolean exists(String token);
    void deleteExpiredTokens();
}

