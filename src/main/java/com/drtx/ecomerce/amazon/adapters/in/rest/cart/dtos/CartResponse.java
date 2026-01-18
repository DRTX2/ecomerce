package com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record CartResponse(
                UUID uuid,
                @NotEmpty List<CartItemDto> items) {
}
