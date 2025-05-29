package com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos;

import com.drtx.ecomerce.amazon.core.model.Product;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CartResponse(
        Long id,
        @NotEmpty List<Product> products
) {
}
