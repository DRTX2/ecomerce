package com.drtx.ecomerce.amazon.core.ports.out.security;

import com.drtx.ecomerce.amazon.core.model.user.User;

public interface TokenProvider {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String extractUsername(String token);

    boolean isTokenValid(String token, User user);

    boolean isRefreshTokenValid(String refreshToken);

    long getAccessTokenExpirationMs();

    long getRefreshTokenExpirationMs();
}
