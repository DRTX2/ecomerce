package com.drtx.ecomerce.amazon.core.model.order;

import com.drtx.ecomerce.amazon.core.model.pagination.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record OrderSearchCriteria(
        Optional<UUID> userUuid,
        Optional<OrderState> orderState,

        LocalDateTime from,
        LocalDateTime to,

        Long minTotal,
        Long maxTotal,

        //Pagination & Sort
        PageRequest pageRequest
) {
}
