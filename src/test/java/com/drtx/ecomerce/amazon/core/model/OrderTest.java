package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.order.Order;
import com.drtx.ecomerce.amazon.core.model.order.OrderItem;
import com.drtx.ecomerce.amazon.core.model.order.OrderState;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    @DisplayName("Should create order correctly")
    void testOrderCreation() {
        UUID userUuid = UUID.randomUUID();
        User user = new User(1L, userUuid, "David", "email", "pass", "address", "123", UserRole.USER, true, false);
        Product product = new Product(1L, UUID.randomUUID(), "Phone", "Smartphone", new BigDecimal("500"), null, null,
                List.of("phone.png"), "SKU-PH", 5, ProductStatus.ACTIVE, "phone", LocalDateTime.now(), null);

        UUID orderUuid = UUID.randomUUID();
        Order order = new Order(1L, orderUuid, user, null, new BigDecimal("500"), OrderState.PENDING, LocalDateTime.now(), null,
                List.of());

        assertEquals(1L, order.getId());
        assertEquals(orderUuid, order.getUuid());
        assertEquals(user, order.getUser());
        assertEquals(OrderState.PENDING, order.getOrderState());
    }

    @Test
    @DisplayName("Should update status correctly")
    void testUpdateStatus() {
        UUID orderUuid = UUID.randomUUID();
        Order order = new Order(2L, orderUuid, null, null, BigDecimal.ZERO, OrderState.CANCELED, null, null, List.of());
        order.setOrderState(OrderState.DELIVERED);
        assertEquals(OrderState.DELIVERED, order.getOrderState());
    }
}
