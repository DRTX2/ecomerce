package com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemDto(
        @NotNull @Positive Long productId,
        @NotNull @Positive Integer quantity) {
}
