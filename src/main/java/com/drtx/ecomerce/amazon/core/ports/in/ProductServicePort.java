package com.drtx.ecomerce.amazon.core.ports.in;

import com.drtx.ecomerce.amazon.core.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductServicePort {
    Product createProduct(Product product);
    Optional<Product> getProductById(Long id);
    List<Product> getAllProducts();
    Optional<Product> updateProduct(Product product);
    void deleteProduct(Long id);
}
