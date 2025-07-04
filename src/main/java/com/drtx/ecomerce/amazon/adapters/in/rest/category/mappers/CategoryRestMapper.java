package com.drtx.ecomerce.amazon.adapters.in.rest.category.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryResponse;
import com.drtx.ecomerce.amazon.core.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryRestMapper {
    Category toDomain(CategoryRequest request);

    @Mapping(target = "id", source = "id")
    CategoryResponse toResponse(Category category);
}


