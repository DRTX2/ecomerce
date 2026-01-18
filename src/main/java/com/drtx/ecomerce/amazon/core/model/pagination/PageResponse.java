package com.drtx.ecomerce.amazon.core.model.pagination;

import java.util.List;

/**
 * Generic paginated response wrapper.
 * Follows common pagination patterns used in REST APIs.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean empty
) {
    public static <T> PageResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements
    ) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        boolean empty = content.isEmpty();

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                first,
                last,
                empty
        );
    }
}

