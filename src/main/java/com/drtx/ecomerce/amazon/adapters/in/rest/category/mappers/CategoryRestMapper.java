package com.drtx.ecomerce.amazon.adapters.in.rest.category.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryResponse;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toDomain(CategoryRequest request);

    @Mapping(target = "uuid", source = "uuid")
    CategoryResponse toResponse(Category category);
}


