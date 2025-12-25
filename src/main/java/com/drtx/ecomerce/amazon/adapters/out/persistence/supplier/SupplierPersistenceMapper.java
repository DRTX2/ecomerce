package com.drtx.ecomerce.amazon.adapters.out.persistence.supplier;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.supplier.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ProductPersistenceMapper.class })
public interface SupplierPersistenceMapper {
    Supplier toDomain(SupplierEntity entity);

    SupplierEntity toEntity(Supplier domain);
}
