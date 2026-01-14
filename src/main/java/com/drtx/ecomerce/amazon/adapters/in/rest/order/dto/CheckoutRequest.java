package com.drtx.ecomerce.amazon.adapters.in.rest.order.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para la solicitud de checkout que convierte un carrito en una orden
 */
public record CheckoutRequest(
        @NotNull Long cartId) {
}
