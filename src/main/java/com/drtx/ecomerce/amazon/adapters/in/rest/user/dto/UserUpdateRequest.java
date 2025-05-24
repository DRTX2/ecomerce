package com.drtx.ecomerce.amazon.adapters.in.rest.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String role,
        @Nullable String address,
        @Nullable String phone
) {
}
