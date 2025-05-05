package com.drtx.ecomerce.amazon.adapters.out.persistence.category;

import com.drtx.ecomerce.amazon.core.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryPersistenceMapper {
    Category toDomain(CategoryEntity entity);
    CategoryEntity toEntity(Category domain);
}


