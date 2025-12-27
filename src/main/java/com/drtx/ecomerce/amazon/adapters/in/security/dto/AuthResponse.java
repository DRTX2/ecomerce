package com.drtx.ecomerce.amazon.adapters.in.security.dto;

public record AuthResponse(
                UserResponse user,
                AuthTokens tokens) {
}