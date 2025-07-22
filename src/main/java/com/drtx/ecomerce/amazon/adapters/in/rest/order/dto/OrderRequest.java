package com.drtx.ecomerce.amazon.adapters.in.rest.order.dto;

import com.drtx.ecomerce.amazon.core.model.OrderState;
import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderRequest(
        Long id,
        User user,
        List<Product>products,
        BigDecimal total,
        OrderState orderState,
        LocalDateTime createdAt,
        LocalDateTime deliveredAt,
        String paymentType
) {
}
