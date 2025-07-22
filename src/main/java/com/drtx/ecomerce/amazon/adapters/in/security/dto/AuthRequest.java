package com.drtx.ecomerce.amazon.adapters.in.security.dto;

public record AuthRequest(
        String email,
        String password
) {
}
