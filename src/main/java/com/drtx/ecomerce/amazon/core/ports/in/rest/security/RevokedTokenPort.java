package com.drtx.ecomerce.amazon.core.ports.in.rest.security;

public interface RevokedTokenPort {
    void save(String token);
    boolean exists(String token);
    void deleteExpiredTokens();
}
