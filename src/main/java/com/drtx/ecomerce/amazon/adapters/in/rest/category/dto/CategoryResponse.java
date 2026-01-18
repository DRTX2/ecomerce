package com.drtx.ecomerce.amazon.adapters.in.rest.category.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID uuid,
        String name,
        String description
) {
}
