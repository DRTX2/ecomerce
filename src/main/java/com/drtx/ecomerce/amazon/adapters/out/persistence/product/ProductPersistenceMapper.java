package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.core.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProductMapperHelper.class)
public interface ProductPersistenceMapper {
    @Mapping(source = "images", target = "images", qualifiedByName = "mapToUrls")
    Product toDomain(ProductEntity entity);

    @Mapping(source = "images", target = "images", qualifiedByName = "mapToEntities")
    ProductEntity toEntity(Product product);
}
