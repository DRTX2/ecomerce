package com.drtx.ecomerce.amazon.adapters.out.persistence.inventory;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.product.Inventory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ProductPersistenceMapper.class })
public interface InventoryPersistenceMapper {
    Inventory toDomain(InventoryEntity entity);

    List<Inventory> toDomainList(List<InventoryEntity> entities);

    InventoryEntity toEntity(Inventory domain);
}
