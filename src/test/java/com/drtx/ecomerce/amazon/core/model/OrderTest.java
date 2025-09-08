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
        Product product = new Product(1L, "Phone", "Smartphone", new BigDecimal("500"), 5, null, null, null);

        Order order = new Order(
                1L, user, List.of(product),
                new BigDecimal("500"),
                OrderState.PENDING,
                LocalDateTime.now(),
                null,
                "CREDIT_CARD"
        );

        assertEquals(1L, order.getId());
        assertEquals(OrderState.PENDING, order.getState());
        assertEquals("CREDIT_CARD", order.getPaymentType());
        assertEquals(1, order.getProducts().size());
    }

    @Test
    public void testSetters() {
        Order order = new Order(2L, null, null, BigDecimal.ZERO, OrderState.CANCELED, null, null, "PAYPAL");
        order.setState(OrderState.DELIVERED);

        assertEquals(OrderState.DELIVERED, order.getState());
    }
}
