package com.drtx.ecomerce.amazon.adapters.in.rest.product.dto;

import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for product search request with optional filters
 */
public record ProductSearchRequest(
        // Text search
        String searchTerm,

        // Category filter
        UUID categoryUuid,

        // Status filter
        ProductStatus status,

        // Price range
        BigDecimal minPrice,
        BigDecimal maxPrice,

        // Rating range
        BigDecimal minRating,
        BigDecimal maxRating,

        // Special filters
        Boolean onSale,
        Boolean inStock,
        Boolean featuredOnly,

        // Pagination
        Integer page,
        Integer size,

        // Sorting
        String sortBy,
        String sortDirection
) {
    public ProductSearchRequest {
        // Default values
        page = page != null ? page : 0;
        size = size != null ? size : 20;
        sortBy = sortBy != null ? sortBy : "createdAt";
        sortDirection = sortDirection != null ? sortDirection : "DESC";
    }
}

