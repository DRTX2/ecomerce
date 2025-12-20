package com.drtx.ecomerce.amazon.adapters.in.rest.favorite.dto;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;

import java.time.LocalDateTime;

public record FavoriteResponse(
    Long id,
    UserResponse user,
    ProductResponse product,
    LocalDateTime createdAt
) {}
