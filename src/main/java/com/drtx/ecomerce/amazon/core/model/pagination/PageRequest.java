package com.drtx.ecomerce.amazon.core.model.pagination;

import java.util.Optional;

public record PageRequest(
        int page,
        int size,
        Optional<Sort> sort
) {
    public record Sort(
            String field,
            SortDirection direction
    ) {}

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, Optional.empty());
    }
}