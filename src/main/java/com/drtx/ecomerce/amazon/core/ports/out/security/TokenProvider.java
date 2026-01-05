package com.drtx.ecomerce.amazon.core.ports.out.security;

import com.drtx.ecomerce.amazon.core.model.user.User;

public interface TokenProvider {
    String generateToken(User user);

    String extractUsername(String token);

    boolean isTokenValid(String token, User user);
}
