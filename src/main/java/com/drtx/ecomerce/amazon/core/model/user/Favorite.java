package com.drtx.ecomerce.amazon.core.model.user;

import com.drtx.ecomerce.amazon.core.model.product.Product;

import java.time.LocalDateTime;

public class Favorite {
    private Long id;
    private User user;
    private Product product;
    private LocalDateTime createdAt;

    public Favorite() {
    }

    public Favorite(Long id, User user, Product product, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.createdAt = createdAt;
    }

    public static FavoriteBuilder builder() {
        return new FavoriteBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void initializeDefaults() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static class FavoriteBuilder {
        private Long id;
        private User user;
        private Product product;
        private LocalDateTime createdAt;

        FavoriteBuilder() {
        }

        public FavoriteBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FavoriteBuilder user(User user) {
            this.user = user;
            return this;
        }

        public FavoriteBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public FavoriteBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Favorite build() {
            return new Favorite(id, user, product, createdAt);
        }
    }
}
