package com.drtx.ecomerce.amazon.adapters.in.rest.cart.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.cart.dtos.CartResponse;
import com.drtx.ecomerce.amazon.core.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartRestMapper {
    Cart toDomain(CartRequest request);
    CartResponse toResponse(Cart domain);
}
