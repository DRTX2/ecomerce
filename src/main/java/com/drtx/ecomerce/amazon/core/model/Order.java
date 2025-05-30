package com.drtx.ecomerce.amazon.core.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private User user;
    private List<Product> products;
    private Double total;
    private OrderState orderState;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private Payment payment;

    public Order(Long id, User user, List<Product> products, Double total, OrderState orderState, LocalDateTime createdAt, LocalDateTime deliveredAt, Payment payment) {
        this.id = id;
        this.user = user;
        this.products = products;
        this.total = total;
        this.orderState = orderState;
        this.createdAt = createdAt;
        this.deliveredAt = deliveredAt;
        this.payment = payment;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
