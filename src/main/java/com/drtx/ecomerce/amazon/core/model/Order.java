package com.drtx.ecomerce.amazon.core.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private User user;
    private List<OrderItem> items;
    private BigDecimal total;
    private OrderState orderState;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private List<Discount> appliedDiscounts;

    public Order(Long id, User user, List<OrderItem> items, BigDecimal total, OrderState orderState,
            LocalDateTime createdAt, LocalDateTime deliveredAt, List<Discount> appliedDiscounts) {
        this.id = id;
        this.user = user;
        this.items = items;
        this.total = total;
        this.orderState = orderState;
        this.createdAt = createdAt;
        this.deliveredAt = deliveredAt;
        this.appliedDiscounts = appliedDiscounts;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
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

    public List<Discount> getAppliedDiscounts() {
        return appliedDiscounts;
    }

    public void setAppliedDiscounts(List<Discount> appliedDiscounts) {
        this.appliedDiscounts = appliedDiscounts;
    }
}
