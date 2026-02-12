package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.pagination.PageResponse;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductSearchCriteria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductUseCasePort {
    Product createProduct(Product product);

    Optional<Product> getProductByUuid(UUID uuid);

    /**
     * Search products with filters and pagination
     */
    PageResponse<Product> searchProducts(ProductSearchCriteria searchCriteria);

    /**
     * Get popular products (most favorited and highest rated), this might need more filters in the future (e.g. category, time range)
     * @param limit Maximum number of products to return
     */
    List<Product> getPopularProducts(int limit);

    /**
     * Get products on sale/deals (products with discounts)
     * @param limit Maximum number of products to return
     */
    List<Product> getDealsProducts(int limit);

    Product updateProduct(UUID uuid, Product product);

    void deleteProductByUuid(UUID uuid);
}


