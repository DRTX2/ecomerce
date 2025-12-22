package com.drtx.ecomerce.amazon.adapters.out.persistence.discount;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ProductPersistenceMapper.class })
public interface DiscountPersistenceMapper {
    Discount toDomain(DiscountEntity entity);

    DiscountEntity toEntity(Discount domain);
}
