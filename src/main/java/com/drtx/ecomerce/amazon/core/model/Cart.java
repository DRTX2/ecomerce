package com.drtx.ecomerce.amazon.core.model;

import java.util.List;

public class Cart {
    private Long id;
    private User user;
    private List<CartItem> items;

    public Cart() {
    }

    public Cart(User user, List<CartItem> items) {
        this.user = user;
        this.items = items;
    }

    public Cart(Long id, User user, List<CartItem> items) {
        this.id = id;
        this.user = user;
        this.items = items;
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

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
