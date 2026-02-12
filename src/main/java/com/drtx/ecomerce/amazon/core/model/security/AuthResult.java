package com.drtx.ecomerce.amazon.core.model.security;

import com.drtx.ecomerce.amazon.core.model.user.User;
// this might be also a class
public record AuthResult(
        User user,
        String accessToken,
        String refreshToken,
        Long expiresInMs
) {
}
