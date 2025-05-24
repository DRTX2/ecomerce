package com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.core.model.Order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderRestMapper {
    Order toDomain(OrderRequest request);

    //@Mapping(target = "id", source = "id")
    OrderResponse toResponse(Order domain);
}
