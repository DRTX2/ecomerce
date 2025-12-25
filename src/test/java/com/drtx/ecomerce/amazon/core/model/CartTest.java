package com.drtx.ecomerce.amazon.core.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    @Test
    public void testCreateCartWithConstructor() {
        User user = new User(1L, "David", "david@email.com", "1234", "Ambato", "0987654321", UserRole.USER);
        Product p = new Product(1L, "Laptop", "Gaming laptop", new BigDecimal("1200"), null, null,
                List.of("laptop.jpg"));
        Cart cart = new Cart(1L, user, null);
        CartItem item = new CartItem(1L, cart, p, 2);
        List<CartItem> items = List.of(item);
        cart.setItems(items);

        assertEquals(1L, cart.getId());
        assertEquals(user, cart.getUser());
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testSettersAndGetters() {
        Cart cart = new Cart();
        cart.setId(10L);

        assertEquals(10L, cart.getId());
        assertNull(cart.getUser());
        assertNull(cart.getItems());
    }
}