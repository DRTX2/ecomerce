package com.drtx.ecomerce.amazon.core.model.security;

import java.time.Instant;

public class Token {
    private final String value;
    private final Instant expiresAt;
    private final Instant revokedAt;

    public Token(String value, Instant expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
        this.revokedAt = Instant.now();
    }

    public String getValue() { return value; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
}