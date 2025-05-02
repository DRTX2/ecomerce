package com.drtx.ecomerce.amazon.adapters.in.rest.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String role,
        @NotBlank String address,
        @NotBlank String phone,
        @Size(min=8) String password
) {
}
