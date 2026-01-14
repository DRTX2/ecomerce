package com.drtx.ecomerce.amazon.adapters.in.rest.order.dto;

import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el estado de una orden
 */
public record UpdateOrderStateRequest(
        @NotNull OrderState newState) {
}
