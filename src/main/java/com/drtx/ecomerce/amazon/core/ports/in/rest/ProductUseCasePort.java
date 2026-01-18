package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductUseCasePort {
    Product createProduct(Product product);
    Optional<Product> getProductByUuid(UUID uuid);
    List<Product> getAllProducts();
    Product updateProduct(UUID uuid, Product product);
    void deleteProductByUuid(UUID uuid);
}
