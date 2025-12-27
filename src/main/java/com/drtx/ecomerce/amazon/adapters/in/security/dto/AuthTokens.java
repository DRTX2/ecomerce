package com.drtx.ecomerce.amazon.adapters.in.security.dto;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        Long expiresAt) {
}
