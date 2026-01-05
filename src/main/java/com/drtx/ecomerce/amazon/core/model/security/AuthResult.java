package com.drtx.ecomerce.amazon.core.model.security;

import com.drtx.ecomerce.amazon.core.model.user.User;

public record AuthResult(User user, String accessToken, String refreshToken, Long expiresInMs) {
}
