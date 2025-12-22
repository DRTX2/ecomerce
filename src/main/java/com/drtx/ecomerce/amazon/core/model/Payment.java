package com.drtx.ecomerce.amazon.core.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private Order order;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod method;
    private LocalDateTime paymentDate;

    public Payment() {
    }

    public Payment(Long id, Order order, BigDecimal amount, PaymentStatus status, PaymentMethod method,
            LocalDateTime paymentDate) {
        this.id = id;
        this.order = order;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.paymentDate = paymentDate;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}
