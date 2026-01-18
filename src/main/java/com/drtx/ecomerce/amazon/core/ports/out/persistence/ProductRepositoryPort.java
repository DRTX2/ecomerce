package com.drtx.ecomerce.amazon.core.ports.out.persistence;

import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductSearchCriteria;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);

    Optional<Product> findById(Long id);  // Legacy support
    Optional<Product> findByUuid(UUID uuid);

    /**
     * Search products with filters and pagination
     */
    Page<Product> searchProducts(ProductSearchCriteria criteria);

    /**
     * Find popular products (by favorites count and rating)
     */
    List<Product> findPopularProducts(int limit);

    /**
     * Find products with active deals/discounts
     */
    List<Product> findDealsProducts(int limit);

    Product updateByUuid(UUID uuid, Product productToUPdate);
    void deleteByUuid(UUID uuid);
}


