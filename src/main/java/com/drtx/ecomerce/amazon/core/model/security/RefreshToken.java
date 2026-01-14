package com.drtx.ecomerce.amazon.core.model.security;

import java.time.Instant;

/**
 * Modelo de dominio para Refresh Token.
 * Simplificado sin Builder ni Lombok para consistencia con el resto del
 * dominio.
 */
public class RefreshToken {
    private Long id;
    private String token;
    private String userEmail;
    private Instant expiryDate;
    private boolean revoked;

    public RefreshToken() {
    }

    public RefreshToken(Long id, String token, String userEmail, Instant expiryDate, boolean revoked) {
        this.id = id;
        this.token = token;
        this.userEmail = userEmail;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
