package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findByUuid(UUID uuid);
    List<Product> findAll();
    Product updateByUuid(UUID uuid, Product productToUPdate);
    void deleteByUuid(UUID uuid);
}
