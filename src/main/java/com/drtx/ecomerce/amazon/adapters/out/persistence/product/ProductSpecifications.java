package com.drtx.ecomerce.amazon.adapters.out.persistence.product;

import com.drtx.ecomerce.amazon.adapters.out.persistence.category.CategoryEntity;
import com.drtx.ecomerce.amazon.adapters.out.persistence.discount.DiscountEntity;
import com.drtx.ecomerce.amazon.core.model.product.ProductSearchCriteria;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Specifications for dynamic product search queries.
 * Allows building complex queries with multiple optional filters.
 */
public class ProductSpecifications {

    private ProductSpecifications() {
        // Utility class
    }

    /**
     * Creates a specification based on search criteria
     */
    public static Specification<ProductEntity> withCriteria(ProductSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Text search (search in name, description, SKU)
            criteria.searchTerm().ifPresent(term -> {
                String likePattern = "%" + term.toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), likePattern);
                Predicate descPredicate = cb.like(cb.lower(root.get("description")), likePattern);
                Predicate skuPredicate = cb.like(cb.lower(root.get("sku")), likePattern);
                predicates.add(cb.or(namePredicate, descPredicate, skuPredicate));
            });

            // Category filter (by UUID)
            criteria.categoryUuid().ifPresent(categoryUuid -> {
                Join<ProductEntity, CategoryEntity> categoryJoin = root.join("category");
                predicates.add(cb.equal(categoryJoin.get("uuid"), categoryUuid));
            });

            // Status filter
            criteria.status().ifPresent(status ->
                    predicates.add(cb.equal(root.get("status"), status))
            );

            // Price range
            criteria.minPrice().ifPresent(minPrice ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice))
            );
            criteria.maxPrice().ifPresent(maxPrice ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice))
            );

            // Rating range
            criteria.minRating().ifPresent(minRating ->
                    predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), minRating))
            );
            criteria.maxRating().ifPresent(maxRating ->
                    predicates.add(cb.lessThanOrEqualTo(root.get("averageRating"), maxRating))
            );

            // In stock filter
            criteria.inStock().ifPresent(inStock -> {
                if (inStock) {
                    predicates.add(cb.greaterThan(root.get("stockQuantity"), 0));
                } else {
                    predicates.add(cb.equal(root.get("stockQuantity"), 0));
                }
            });

            // On Sale filter (products with active discounts/deals)
            criteria.onSale().ifPresent(onSale -> {
                if (onSale && query != null) {
                    // Subquery to find products that have active discounts
                    Subquery<Long> discountSubquery = query.subquery(Long.class);
                    Root<DiscountEntity> discountRoot = discountSubquery.from(DiscountEntity.class);
                    Join<DiscountEntity, ProductEntity> discountProducts = discountRoot.join("applicableProducts");

                    discountSubquery.select(discountProducts.get("id"))
                            .where(cb.and(
                                    cb.equal(discountProducts.get("id"), root.get("id")),
                                    cb.or(
                                            cb.isNull(discountRoot.get("expirationDate")),
                                            cb.greaterThan(discountRoot.get("expirationDate"), LocalDateTime.now())
                                    )
                            ));

                    predicates.add(cb.exists(discountSubquery));
                }
            });

            // Featured filter (could be based on a 'featured' flag or high rating)
            criteria.featuredOnly().ifPresent(featured -> {
                if (featured) {
                    // Featured products are those with high rating (4.0+) and in stock
                    predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), new java.math.BigDecimal("4.0")));
                    predicates.add(cb.greaterThan(root.get("stockQuantity"), 0));
                }
            });

            // Always show only ACTIVE products in public searches (unless status is explicitly set)
            if (criteria.status().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Find products by category UUID
     */
    public static Specification<ProductEntity> hasCategoryUuid(UUID categoryUuid) {
        return (root, query, cb) -> {
            Join<ProductEntity, CategoryEntity> categoryJoin = root.join("category");
            return cb.equal(categoryJoin.get("uuid"), categoryUuid);
        };
    }

    /**
     * Find active products only
     */
    public static Specification<ProductEntity> isActive() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), ProductStatus.ACTIVE);
    }

    /**
     * Find products in stock
     */
    public static Specification<ProductEntity> inStock() {
        return (root, query, cb) ->
                cb.greaterThan(root.get("stockQuantity"), 0);
    }

    /**
     * Text search in name, description, or SKU
     */
    public static Specification<ProductEntity> searchText(String searchTerm) {
        return (root, query, cb) -> {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(root.get("name")), likePattern);
            Predicate descPredicate = cb.like(cb.lower(root.get("description")), likePattern);
            Predicate skuPredicate = cb.like(cb.lower(root.get("sku")), likePattern);
            return cb.or(namePredicate, descPredicate, skuPredicate);
        };
    }
}



