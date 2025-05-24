package com.drtx.ecomerce.amazon.adapters.out.persistence.category;

import com.drtx.ecomerce.amazon.adapters.out.persistence.product.ProductPersistenceMapper;
import com.drtx.ecomerce.amazon.core.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductPersistenceMapper.class)
public interface CategoryPersistenceMapper {
    Category toDomain(CategoryEntity entity);
    CategoryEntity toEntity(Category domain);
}


