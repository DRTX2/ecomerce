package com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

import java.util.List;

public record CartRequest(
                @NotEmpty List<@Valid CartItemDto> items) {
}
