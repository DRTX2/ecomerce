package com.drtx.ecomerce.amazon.core.model.order;

import com.drtx.ecomerce.amazon.core.model.user.User;

import java.util.List;
import java.util.UUID;

public class Cart {
    private Long id;
    private UUID uuid;
    private User user;
    private List<CartItem> items;

    public Cart() {
    }

    public Cart(User user, List<CartItem> items) {
        this.user = user;
        this.items = items;
    }

    public Cart(Long id, UUID uuid, User user, List<CartItem> items) {
        this.id = id;
        this.uuid = uuid;
        this.user = user;
        this.items = items;
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
