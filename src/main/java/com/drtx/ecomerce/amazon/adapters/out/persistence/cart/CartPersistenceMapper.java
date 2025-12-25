package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductMapperHelper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Cart;
import com.drtx.ecomerce.amazon.core.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ProductPersistenceMapper.class,
        ProductMapperHelper.class,
        UserPersistenceMapper.class
})
public interface CartPersistenceMapper {
    @Mapping(target = "items", source = "items")
    Cart toDomain(CartEntity entity);

    @Mapping(target = "items", source = "items")
    CartEntity toEntity(Cart domain);

    @Mapping(target = "cart", ignore = true)
    CartItem toDomain(CartItemEntity entity);

    @Mapping(target = "cart", ignore = true)
    CartItemEntity toEntity(CartItem domain);
}
