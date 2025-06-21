package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.core.model.Product;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapperHelper {
    @Named("mapToProducts")
    public List<Product> mapToProducts(List<ProductEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(entity -> {
            Product p = new Product();
            p.setId(entity.getId());
            return p;
        }).toList();
    }

    @Named("mapToProductEntities")
    public List<ProductEntity> mapToProductEntities(List<Product> products) {
        if (products == null) return null;
        return products.stream().map(product -> {
            ProductEntity e = new ProductEntity();
            e.setId(product.getId());
            return e;
        }).toList();
    }

    @Named("mapToUrls")
    public List<String> mapToUrls(List<ProductImageEntity> images) {
        if (images == null) return null;
        return images.stream().map(ProductImageEntity::getUrl).toList();
    }

    @Named("mapToEntities")
    public List<ProductImageEntity> mapToEntities(List<String> urls) {
        if (urls == null) return null;
        return urls.stream().map(url -> {
            ProductImageEntity img = new ProductImageEntity();
            img.setUrl(url);
            return img;
        }).toList();
    }
}
