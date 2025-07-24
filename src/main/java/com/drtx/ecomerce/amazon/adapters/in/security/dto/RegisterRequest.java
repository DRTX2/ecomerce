package com.drtx.ecomerce.amazon.adapters.in.security.dto;


import com.drtx.ecomerce.amazon.core.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        UserRole role,
        @NotBlank String address,
        @NotBlank String phone,
        @Size(min=8) String password
) {
}
