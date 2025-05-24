package com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.core.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductRestMapper {
    Product toDomain(ProductRequest request);
    @Mapping(target = "id", source = "id")
    ProductResponse toResponse(Product domain);
}
