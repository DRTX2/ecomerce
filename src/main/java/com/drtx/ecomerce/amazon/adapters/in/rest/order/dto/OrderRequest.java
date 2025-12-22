package com.drtx.ecomerce.amazon.adapters.in.rest.order.dto;

import com.drtx.ecomerce.amazon.core.model.OrderState;
import com.drtx.ecomerce.amazon.core.model.Product;
import com.drtx.ecomerce.amazon.core.model.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderRequest(
                Long id,
                @NotNull User user,
                @NotEmpty List<Product> products,
                @NotNull BigDecimal total,
                @NotNull OrderState orderState,
                LocalDateTime createdAt,
                LocalDateTime deliveredAt,
                String paymentType) {
}
