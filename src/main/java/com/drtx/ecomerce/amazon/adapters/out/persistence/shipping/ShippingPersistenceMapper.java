package com.drtx.ecomerce.amazon.adapters.out.persistence.shipping;

import com.drtx.ecomerce.amazon.adapters.out.persistence.order.OrderPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.shipping.Shipping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { OrderPersistenceMapper.class })
public interface ShippingPersistenceMapper {
    Shipping toDomain(ShippingEntity entity);

    ShippingEntity toEntity(Shipping domain);
}
