package com.drtx.ecomerce.amazon.adapters.in.rest.order.dto;

import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID uuid,
        List<OrderItemDto> items,
        BigDecimal total,
        OrderState orderState,
        LocalDateTime createdAt,
        LocalDateTime deliveredAt) {
}
