package com.drtx.ecomerce.amazon.core.model.shipping;

import com.drtx.ecomerce.amazon.core.model.order.Order;

import java.time.LocalDateTime;

public class Shipping {
    private Long id;
    private Order order;
    private String deliveryAddress; // also could be a complex Address object
    private ShippingStatus status;
    private String carrier; // in big systems, this could be a Carrier entity
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;

    public Shipping() {
    }

    public Shipping(Long id, Order order, String deliveryAddress, ShippingStatus status, String carrier,
                    String trackingNumber, LocalDateTime estimatedDelivery) {
        this.id = id;
        this.order = order;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.estimatedDelivery = estimatedDelivery;
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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public ShippingStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingStatus status) {
        this.status = status;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
}
