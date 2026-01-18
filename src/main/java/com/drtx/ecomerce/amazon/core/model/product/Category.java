package com.drtx.ecomerce.amazon.core.model.product;

import java.util.List;
import java.util.UUID;

public class Category {
    private Long id;
    private UUID uuid;
    private String name;
    private String description;
    private List<Product> products;

    public Category() {
    }

    public Category(Long id, UUID uuid, String name, String description, List<Product> products) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
