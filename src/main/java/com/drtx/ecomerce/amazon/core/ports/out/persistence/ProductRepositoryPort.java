package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductPage;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    ProductPage searchActive(String query, Long categoryId, int page, int size);
    Product updateById(Long id, Product productToUPdate);
    boolean reserveStock(Long productId, int quantity);
    void delete(Long id);
}
