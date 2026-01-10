package com.drtx.ecomerce.amazon.core.model.product;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainException;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Category category;
    private BigDecimal averageRating;
    private List<String> images;

    // New fields
    private String sku;
    private Integer stockQuantity;
    private ProductStatus status;
    private String slug;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    public Product() {
    }

    public Product(Long id, String name, String description, BigDecimal price, Category category,
            BigDecimal averageRating, List<String> images, String sku, Integer stockQuantity,
            ProductStatus status, String slug, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.averageRating = averageRating;
        this.images = images;
        this.sku = sku;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.slug = slug;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void validate() {
        if (this.name == null || this.name.isBlank()) {
            throw new DomainException("Product name cannot be null or blank");
        }
        if (this.price == null || this.price.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Product price cannot be negative");
        }
        if (this.sku == null || this.sku.isBlank()) {
            throw new DomainException("Product SKU is required");
        }
        if (this.stockQuantity == null || this.stockQuantity < 0) {
            throw new DomainException("Product stock cannot be negative");
        }
        if (this.status == null) {
            this.status = ProductStatus.DRAFT; // Default to DRAFT if null
        }
    }
}