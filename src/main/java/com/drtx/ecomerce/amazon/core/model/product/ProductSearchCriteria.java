package com.drtx.ecomerce.amazon.core.model.product;

import com.drtx.ecomerce.amazon.core.model.pagination.PageRequest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Criteria for searching and filtering products.
 * Supports pagination, filtering by category, price range, rating, status, and special flags.
 */
public record ProductSearchCriteria(
        // Text search
        Optional<String> searchTerm,        // Search in name, description, SKU

        // Category filter
        Optional<UUID> categoryUuid,

        // Status filter
        Optional<ProductStatus> status,

        // Price range
        Optional<BigDecimal> minPrice,
        Optional<BigDecimal> maxPrice,

        // Rating range
        Optional<BigDecimal> minRating,
        Optional<BigDecimal> maxRating,

        // Special filters
        Optional<Boolean> onSale,           // Has active discounts/deals
        Optional<Boolean> inStock,          // Has stock > 0
        Optional<Boolean> featuredOnly,     // Featured products

        // Pagination & Sort
        PageRequest pageRequest
) {

    /**
     * Builder pattern for easier construction with optional parameters
     */
    public static class Builder {
        private Optional<String> searchTerm = Optional.empty();
        private Optional<UUID> categoryUuid = Optional.empty();
        private Optional<ProductStatus> status = Optional.empty();
        private Optional<BigDecimal> minPrice = Optional.empty();
        private Optional<BigDecimal> maxPrice = Optional.empty();
        private Optional<BigDecimal> minRating = Optional.empty();
        private Optional<BigDecimal> maxRating = Optional.empty();
        private Optional<Boolean> onSale = Optional.empty();
        private Optional<Boolean> inStock = Optional.empty();
        private Optional<Boolean> featuredOnly = Optional.empty();
        private PageRequest pageRequest = PageRequest.of(0, 20);

        public Builder searchTerm(String searchTerm) {
            this.searchTerm = Optional.ofNullable(searchTerm);
            return this;
        }

        public Builder categoryUuid(UUID categoryUuid) {
            this.categoryUuid = Optional.ofNullable(categoryUuid);
            return this;
        }

        public Builder status(ProductStatus status) {
            this.status = Optional.ofNullable(status);
            return this;
        }

        public Builder priceRange(BigDecimal minPrice, BigDecimal maxPrice) {
            this.minPrice = Optional.ofNullable(minPrice);
            this.maxPrice = Optional.ofNullable(maxPrice);
            return this;
        }

        public Builder ratingRange(BigDecimal minRating, BigDecimal maxRating) {
            this.minRating = Optional.ofNullable(minRating);
            this.maxRating = Optional.ofNullable(maxRating);
            return this;
        }

        public Builder onSale(Boolean onSale) {
            this.onSale = Optional.ofNullable(onSale);
            return this;
        }

        public Builder inStock(Boolean inStock) {
            this.inStock = Optional.ofNullable(inStock);
            return this;
        }

        public Builder featuredOnly(Boolean featuredOnly) {
            this.featuredOnly = Optional.ofNullable(featuredOnly);
            return this;
        }

        public Builder pageRequest(PageRequest pageRequest) {
            this.pageRequest = pageRequest;
            return this;
        }

        public ProductSearchCriteria build() {
            return new ProductSearchCriteria(
                    searchTerm,
                    categoryUuid,
                    status,
                    minPrice,
                    maxPrice,
                    minRating,
                    maxRating,
                    onSale,
                    inStock,
                    featuredOnly,
                    pageRequest
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
