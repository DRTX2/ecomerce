package com.drtx.ecomerce.amazon.core.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    @Test
    public void testCreateCartWithConstructor() {
        User user = new User(1L, "David", "david@email.com", "1234", "Ambato", "0987654321", UserRole.USER);
        Product p = new Product(1L, "Laptop", "Gaming laptop", new BigDecimal("1200"), 10, null, null, List.of("laptop.jpg"));
        List<Product> products = List.of(p);

        Cart cart = new Cart(1L, user, products);

        assertEquals(1L, cart.getId());
        assertEquals(user, cart.getUser());
        assertEquals(1, cart.getProducts().size());
    }

    @Test
    public void testSettersAndGetters() {
        Cart cart = new Cart();
        cart.setId(10L);

        assertEquals(10L, cart.getId());
        assertNull(cart.getUser());
        assertNull(cart.getProducts());
    }
}