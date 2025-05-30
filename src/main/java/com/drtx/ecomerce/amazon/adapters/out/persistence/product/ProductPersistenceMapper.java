package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.core.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {
    @Mapping(source = "images", target = "images", qualifiedByName = "mapToUrls")
    Product toDomain(ProductEntity entity);

    @Mapping(source = "images", target = "images", qualifiedByName = "mapToEntities")
    ProductEntity toEntity(Product product);

    // 👇 MapStruct uses these automatically for nested conversion
    @Named("mapToUrls")
    default List<String> mapToUrls(List<ProductImageEntity> images) {
        if (images == null) return null;
        return images.stream().map(ProductImageEntity::getUrl).toList();
    }

    @Named("mapToEntities")
    default List<ProductImageEntity> mapToEntities(List<String> urls) {
        if (urls == null) return null;
        return urls.stream().map(url -> {
                    ProductImageEntity img = new ProductImageEntity();
                    img.setUrl(url);
                    return img;
                })
                .toList();
    }
}
