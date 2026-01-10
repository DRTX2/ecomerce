package com.drtx.ecomerce.amazon.adapters.in.rest.product.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull Double price,

        @NotNull Integer categoryId,
        @NotNull Double averageRating,
        @Nullable List<String> images,

        @NotBlank String sku,
        @NotNull Integer stockQuantity,
        @Nullable com.drtx.ecomerce.amazon.core.model.product.ProductStatus status,
        @Nullable String slug) {
}
