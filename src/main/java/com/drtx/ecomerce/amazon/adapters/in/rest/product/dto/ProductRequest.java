package com.drtx.ecomerce.amazon.adapters.in.rest.product.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull Double price,
        @NotNull Integer stock,
        @NotNull Integer categoryId,
        @NotNull Double averageRating,
        @Nullable List<String> images
) {
}
