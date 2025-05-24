package com.drtx.ecomerce.amazon.adapters.in.rest.category.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank  String name,
        @Nullable String description
) {
}
