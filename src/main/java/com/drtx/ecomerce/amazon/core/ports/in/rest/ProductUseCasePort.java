package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductPage;

import java.util.List;
import java.util.Optional;

public interface ProductUseCasePort {
    Product createProduct(Product product);
    Optional<Product> getProductById(Long id);
    List<Product> getAllProducts();
    ProductPage searchActiveProducts(String query, Long categoryId, int page, int size);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}
