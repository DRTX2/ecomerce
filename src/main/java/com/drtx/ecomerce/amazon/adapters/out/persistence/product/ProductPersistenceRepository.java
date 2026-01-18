package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductPersistenceRepository extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);

    /**
     * Find popular products based on favorites count and rating.
     * Popularity = favorites count DESC, rating DESC, recency DESC
     */
    @Query("""
    SELECT p
    FROM ProductEntity p
    LEFT JOIN com.drtx.ecomerce.amazon.adapters.out.persistence.favorite.FavoriteEntity f
        ON f.product = p
    WHERE p.status = com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE
      AND p.stockQuantity > 0
    GROUP BY p
    ORDER BY COUNT(f) DESC, p.averageRating DESC, p.createdAt DESC
    """)
    List<ProductEntity> findPopularProducts(Pageable pageable);

    /**
     * Find products with active deals/discounts.
     * A product is considered on deal if it has at least one active discount.
     */
    @Query("""
    SELECT DISTINCT p
    FROM ProductEntity p
    JOIN com.drtx.ecomerce.amazon.adapters.out.persistence.discount.DiscountEntity d
        ON p MEMBER OF d.applicableProducts
    WHERE p.status = com.drtx.ecomerce.amazon.core.model.product.ProductStatus.ACTIVE
      AND p.stockQuantity > 0
      AND (d.expirationDate IS NULL OR d.expirationDate > CURRENT_TIMESTAMP)
    ORDER BY d.value DESC, p.createdAt DESC
    """)
    List<ProductEntity> findProductsWithDeals(Pageable pageable);
}
