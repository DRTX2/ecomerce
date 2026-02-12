package com.drtx.ecomerce.amazon.adapters.out.persistence.utils;

import com.drtx.ecomerce.amazon.core.model.pagination.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageMapper {

    /**
     * Convert domain PageRequest to Spring Data PageRequest
     */

    public static PageRequest buildPageRequest(com.drtx.ecomerce.amazon.core.model.pagination.PageRequest pageRequest) {
        if (pageRequest.sort().isPresent()) {
            var sortSpec = pageRequest.sort().get();
            Sort.Direction direction = sortSpec.direction() == SortDirection.ASC
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, sortSpec.field());
            return PageRequest.of(pageRequest.page(), pageRequest.size(), sort);
        }
        return PageRequest.of(pageRequest.page(), pageRequest.size());
    }
}
