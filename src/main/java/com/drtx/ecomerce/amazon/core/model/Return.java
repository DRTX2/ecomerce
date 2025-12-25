package com.drtx.ecomerce.amazon.core.model;

import java.time.LocalDateTime;

public class Return {
    private Long id;
    private Order order;
    private String reason;
    private ReturnStatus status;
    private LocalDateTime requestedAt;

    public Return() {
    }

    public Return(Long id, Order order, String reason, ReturnStatus status, LocalDateTime requestedAt) {
        this.id = id;
        this.order = order;
        this.reason = reason;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
