package com.drtx.ecomerce.amazon.core.model;

import com.drtx.ecomerce.amazon.core.model.order.Cart;
import com.drtx.ecomerce.amazon.core.model.order.CartItem;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.model.product.ProductStatus;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.model.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    @Test
    public void testCreateCartWithConstructor() {
        User user = new User(1L, UUID.randomUUID(), "David", "david@email.com", "1234", "Ambato", "0987654321", UserRole.USER, true, false);
        Product p = new Product(1L, UUID.randomUUID(), "Laptop", "Gaming laptop", new BigDecimal("1200"), null, null,
                List.of("laptop.jpg"),
                "SKU-CART", 100, ProductStatus.ACTIVE, "slug-cart", LocalDateTime.now(),
                null);
        Cart cart = new Cart(1L, UUID.randomUUID(), user, null);
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

    @Test
    @DisplayName("Should create cart correctly")
    void testCartCreation() {
        UUID userUuid = UUID.randomUUID();
        User user = new User(1L, userUuid, "David", "david@email.com", "1234", "Ambato", "0987654321", UserRole.USER, true, false);
        Product p = new Product(1L, UUID.randomUUID(), "Laptop", "Gaming laptop", new BigDecimal("1200"), null, null,
                List.of("image.png"), "SKU-LP", 10, ProductStatus.ACTIVE, "laptop", LocalDateTime.now(), null);

        UUID cartUuid = UUID.randomUUID();
        Cart cart = new Cart(1L, cartUuid, user, null);

        assertEquals(1L, cart.getId());
        assertEquals(cartUuid, cart.getUuid());
        assertEquals(user, cart.getUser());
    }
}