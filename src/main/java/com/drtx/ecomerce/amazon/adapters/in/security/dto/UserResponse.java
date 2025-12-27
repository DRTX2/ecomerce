package com.drtx.ecomerce.amazon.adapters.in.security.dto;

import com.drtx.ecomerce.amazon.core.model.user.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role) {
}
