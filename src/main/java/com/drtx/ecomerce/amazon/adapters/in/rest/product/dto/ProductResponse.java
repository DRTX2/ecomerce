package com.drtx.ecomerce.amazon.adapters.in.rest.product.dto;

import com.drtx.ecomerce.amazon.core.model.product.Category;

import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Double price,

        Category category,
        Double averageRating,
        List<String> images,

        String sku,
        Integer stockQuantity,
        com.drtx.ecomerce.amazon.core.model.product.ProductStatus status,
        String slug,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt) {
}
