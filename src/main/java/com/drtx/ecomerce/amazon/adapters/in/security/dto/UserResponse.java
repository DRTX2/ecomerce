package com.drtx.ecomerce.amazon.adapters.in.security.dto;

import com.drtx.ecomerce.amazon.core.model.user.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String name,
        String email,
        UserRole role) {
}
