package com.drtx.ecomerce.amazon.core.ports.out;

import com.drtx.ecomerce.amazon.core.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    Product updateById(Long id, Product productToUPdate);
    void delete(Long id);
}
