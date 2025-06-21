package com.drtx.ecomerce.amazon.adapters.out.persistence.cart;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductMapperHelper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.adapters.out.persistence.user.UserPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
            ProductPersistenceMapper.class,
                ProductMapperHelper.class,
            UserPersistenceMapper.class
        })
public interface CartPersistenceMapper {
    @Mapping(source = "products", target = "products", qualifiedByName = "mapToProducts")
    Cart toDomain(CartEntity entity);
    @Mapping(source = "products", target = "products", qualifiedByName = "mapToProductEntities")
    CartEntity toEntity(Cart domain);
}
