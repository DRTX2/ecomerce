package com.drtx.ecomerce.amazon.adapters.in.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank @Email String email,
        @Size(min = 8) String password
) {
}
