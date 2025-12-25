package com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.core.model.order.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartItemDto;
import com.drtx.ecomerce.amazon.core.model.order.CartItem;

@Mapper(componentModel = "spring")
public interface CartRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Cart toDomain(CartRequest request);

    CartResponse toResponse(Cart domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product.id", source = "productId")
    CartItem toDomain(CartItemDto dto);

    @Mapping(target = "productId", source = "product.id")
    CartItemDto toDto(CartItem domain);
}
