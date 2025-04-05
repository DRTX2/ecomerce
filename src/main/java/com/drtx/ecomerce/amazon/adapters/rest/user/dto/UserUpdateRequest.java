package com.drtx.ecomerce.amazon.adapters.rest.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        @Nullable String name,
        @Email String email,
        @Nullable String role,
        @Nullable String address,
        @Nullable String phone
) {
}
