package com.drtx.ecomerce.amazon.core.model.supplier;

import com.drtx.ecomerce.amazon.core.model.product.Product;

import java.util.List;

public class Supplier {
    private Long id;
    private String name;
    private String contact;
    private List<Product> suppliedProducts;

    public Supplier() {
    }

    public Supplier(Long id, String name, String contact, List<Product> suppliedProducts) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.suppliedProducts = suppliedProducts;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<Product> getSuppliedProducts() {
        return suppliedProducts;
    }

    public void setSuppliedProducts(List<Product> suppliedProducts) {
        this.suppliedProducts = suppliedProducts;
    }
}
