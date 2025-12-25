package com.drtx.ecomerce.amazon.core.model.product;

public class Inventory {
    private Long id;
    private Product product;
    private Integer availableQuantity;
    private String warehouseLocation;

    public Inventory() {
    }

    public Inventory(Long id, Product product, Integer availableQuantity, String warehouseLocation) {
        this.id = id;
        this.product = product;
        this.availableQuantity = availableQuantity;
        this.warehouseLocation = warehouseLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }
}
