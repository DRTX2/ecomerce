package com.drtx.ecomerce.amazon.adapters.in.rest.order.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderResponse;
import com.drtx.ecomerce.amazon.core.model.Order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.drtx.ecomerce.amazon.adapters.in.rest.order.dto.OrderItemDto;
import com.drtx.ecomerce.amazon.core.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "appliedDiscounts", ignore = true)
    Order toDomain(OrderRequest request);

    OrderResponse toResponse(Order domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "priceAtPurchase", source = "price")
    OrderItem toDomain(OrderItemDto dto);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "price", source = "priceAtPurchase")
    OrderItemDto toDto(OrderItem domain);
}
