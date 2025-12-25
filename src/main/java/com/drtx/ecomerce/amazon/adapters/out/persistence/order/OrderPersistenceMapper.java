package com.drtx.ecomerce.amazon.adapters.out.persistence.order;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Order;
import com.drtx.ecomerce.amazon.core.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import com.drtx.ecomerce.amazon.core.model.OrderItem;

@Mapper(componentModel = "spring", uses = { ProductPersistenceMapper.class })
public interface OrderPersistenceMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "discounts", target = "appliedDiscounts")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(source = "appliedDiscounts", target = "discounts")
    OrderEntity toEntity(Order domain);

    @Mapping(target = "order", ignore = true)
    OrderItem toDomain(OrderItemEntity entity);

    @Mapping(target = "order", ignore = true)
    OrderItemEntity toEntity(OrderItem domain);
}
