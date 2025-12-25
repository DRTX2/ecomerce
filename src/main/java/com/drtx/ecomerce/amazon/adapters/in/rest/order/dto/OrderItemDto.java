package com.drtx.ecomerce.amazon.adapters.in.rest.order.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        Long productId,
        Integer quantity,
        BigDecimal price) {
}
