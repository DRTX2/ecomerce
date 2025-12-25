package com.drtx.ecomerce.amazon.core.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderCreation() {
        User user = new User(1L, "David", "email", "pass", "address", "123", UserRole.USER);
        Product product = new Product(1L, "Phone", "Smartphone", new BigDecimal("500"), null, null, null);
        Order order = new Order(1L, user, null, new BigDecimal("500"), OrderState.PENDING, LocalDateTime.now(), null,
                List.of());
        OrderItem item = new OrderItem(1L, order, product, 1, new BigDecimal("500"));
        order.setItems(List.of(item));

        assertEquals(1L, order.getId());
        assertEquals(OrderState.PENDING, order.getOrderState());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("500"), order.getItems().get(0).getPriceAtPurchase());
    }

    @Test
    public void testSetters() {
        Order order = new Order(2L, null, null, BigDecimal.ZERO, OrderState.CANCELED, null, null, List.of());
        order.setOrderState(OrderState.DELIVERED);

        assertEquals(OrderState.DELIVERED, order.getOrderState());
    }
}
