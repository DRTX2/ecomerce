package com.drtx.ecomerce.amazon.core.model;

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

    public Product() {
    }

    public Product(Long id, String name, String description, BigDecimal price, Category category,
            BigDecimal averageRating, List<String> images) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.price = price;
        this.category = category;
        this.averageRating = averageRating;
        this.images = images;
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

    public void validate() {
        if (this.name == null || this.name.isBlank()) {
            throw new DomainException("Product name cannot be null or blank");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Product price cannot be negative");
        }
    }
}